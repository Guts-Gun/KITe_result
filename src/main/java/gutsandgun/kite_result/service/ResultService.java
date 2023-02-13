package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.entity.read.Broker;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.ResultTx;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.querydsl.ResultRepositoryCustom;
import gutsandgun.kite_result.repository.read.*;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@Service
@AllArgsConstructor
public class ResultService {
	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository resultSendingRepository;
	private final ReadResultTxRepository readResultTxRepository;
	private final ReadBrokerRepository readBrokerRepository;
	private final ResultTxTransferRepository resultTxTransferRepository;

	private final ResultRepositoryCustom resultRepositoryCustom;

	Long getUsageCap(String userId) {
		return 100L;
	}

	Long findResultSendingId(Long sendingId) {
		return resultSendingRepository.findBySendingId(sendingId).getId();
	}

	Map<Long, ResultTxSuccessDto> getSuccessCntMap(String userId, List<Long> sendingId) {

		Map<Long, List<ResultTxSuccessRateProjection>> successCountProjectionMap =
				resultSendingRepository.getTxSuccessCountGroupByResultSendingByUserIdAndSendingId(userId, sendingId)
						.stream()
						.collect(Collectors.groupingBy(ResultTxSuccessRateProjection::getSendingId));

		Map<Long, ResultTxSuccessDto> successDtoMap = successCountProjectionMap.keySet()
				.stream()
				.collect(Collectors.toMap(key -> key, key -> new ResultTxSuccessDto(key, successCountProjectionMap.get(key))));

		return successDtoMap;
	}

	Map<Long, Long> getLatencyAvgMap(String userId, List<Long> sendingId) {

		Map<Long, Long> txLatencyAvgMap
				= resultTxTransferRepository.getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(userId, sendingId)
				.stream()
				.collect(Collectors.toMap(ResultTxAvgLatencyProjection::getSendingId, ResultTxAvgLatencyProjection::getAvgLatency));

		return txLatencyAvgMap;
	}


	public List<TotalUsageDto> getTotalUsage(String userId) {

		List<TotalUsageDto> totalUsageDtoList = resultSendingRepository.findTotalUsageBySendingTypeAndUserId(userId)
				.stream()
				.map(totalUsage -> new TotalUsageDto(totalUsage.getSendingType(), totalUsage.getTotalUsage(), getUsageCap(userId)))
				.collect(Collectors.toList());

		return totalUsageDtoList;
	}


	public List<SendingShortInfoDto> getTotalSendingShortInfo(String userId) {

		List<Sending> sendingList = readSendingRepository.findByUserId(userId);

		Map<Long, ResultSending> resultSendingMap = resultSendingRepository.findAllByUserId(userId)
				.stream()
				.collect(Collectors.toMap(ResultSending::getSendingId, Function.identity()));

		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(userId, sendingList.stream().map(Sending::getId).toList());


		List<SendingShortInfoDto> sendingShortInfoDtoList = sendingList.stream()
				.map(sending -> {
					return new SendingShortInfoDto(sending, resultSendingMap.get(sending.getId()), successDtoMap.get(sending.getId()));
				})
				.collect(Collectors.toList());

		return sendingShortInfoDtoList;

	}

	public Page<ResultSendingDto> getTotalResultSending(String userId, Pageable pageable) {
		Page<ResultSending> resultSendingPage = resultSendingRepository.findByUserId(userId, pageable);

		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(userId, resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Map<Long, Long> txLatencyAvgMap = getLatencyAvgMap(userId, resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Page<ResultSendingDto> resultSendingDtoList = resultSendingPage.map(resultSending -> ResultSendingDto.toDto(resultSending, successDtoMap.get(resultSending.getSendingId()), txLatencyAvgMap.get(resultSending.getSendingId())));
		System.out.println(resultSendingDtoList);
		return resultSendingDtoList;
	}


	public ResultSendingDto getResultSending(String userId, Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(userId, sendingId);

		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(userId, Collections.singletonList(resultSending.getSendingId()));
		Map<Long, Long> txLatencyAvgMap = getLatencyAvgMap(userId, Collections.singletonList(resultSending.getSendingId()));


		ResultSendingDto resultSendingDto = ResultSendingDto.toDto(resultSending, successDtoMap.get(resultSending.getSendingId()), txLatencyAvgMap.get(resultSending.getSendingId()));
		System.out.println(resultSendingDto);
		return resultSendingDto;
	}


	public ResultBrokerDto getResultSendingBroker(String userId, Long sendingId) {
		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(userId, sendingId);
		List<ResultTx> resultTxList = readResultTxRepository.findByResultSendingId(resultSending.getId());
		Map<String, Long> brokerCount;
		Map<String, Map<Boolean, Long>> brokerSuccessFail;
		Map<String, LongSummaryStatistics> brokerSpeed;

		Map<Long, Broker> brokerMap = readBrokerRepository.findAllMap();
		System.out.println(brokerMap);


		brokerCount = resultTxList.stream()
				.collect(Collectors.groupingBy(ResultTx -> brokerMap.get(ResultTx.getBrokerId()).getName(), Collectors.counting()));

		//tf 둘중 하나없으면 0으로 나오게 추가하기
		brokerSuccessFail = resultTxList.stream()
				.collect(Collectors.groupingBy(ResultTx -> brokerMap.get(ResultTx.getBrokerId()).getName(),
						Collectors.groupingBy(ResultTx::getSuccess, Collectors.counting())));

		//여기도 진짜 심각함
		List<String> tempName = new java.util.ArrayList<>();
		List<Long> tempData = new java.util.ArrayList<>();
		for (String key : brokerSuccessFail.keySet()) {
			System.out.println(key);
			for (Boolean key2 : brokerSuccessFail.get(key).keySet()) {
				System.out.println(key2);
				tempName.add(key + "-" + key2.toString());
				tempData.add(brokerSuccessFail.get(key).get(key2));
			}
		}

		NameDateListDto temp = new NameDateListDto(tempName, tempData);


//		brokerSpeed = resultTxList.stream()
//				.collect(Collectors.groupingBy(ResultTx -> brokerMap.get(ResultTx.getBrokerId()).getName(),
//						Collectors.summarizingLong(ResultTx -> ResultTx.getCompleteTime() - ResultTx.getSendTime())));

//		ResultBrokerDto resultBrokerDto = new ResultBrokerDto(sendingId, brokerCount, temp, brokerSpeed);

		ResultBrokerDto resultBrokerDto = new ResultBrokerDto(sendingId, brokerCount, temp, emptyMap());
		return resultBrokerDto;
	}

	public Page<ResultTxDto> getResultSendingTx(String userId, Pageable pageable, Long sendingId) {
		Page<ResultTx> resultTxPage = readResultTxRepository.findByUserIdAndResultSendingId(userId, findResultSendingId(sendingId), pageable);
		Page<ResultTxDto> resultTxDtoPage = resultTxPage.map(ResultTxDto::toDto);
		return resultTxDtoPage;
	}


	public Page<ResultSendingDto> getFilteredResultSendingList(String userId, SendingType sendingType, String startDt, String endDt, SendingStatus sendingStatus, Pageable pageable) throws ParseException {

		Page<ResultSendingDto> tuplePageList = resultRepositoryCustom.findByRegIdAndSendingTypeAndSuccessAndRegdt(userId, sendingType, startDt, endDt, sendingStatus, pageable);
		List<ResultSendingDto> list = tuplePageList.getContent();
		return new PageImpl<>(list, pageable, tuplePageList.getTotalElements());
	}

}
