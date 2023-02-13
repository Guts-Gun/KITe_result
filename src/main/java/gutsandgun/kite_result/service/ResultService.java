package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.entity.read.Broker;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.ResultTx;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.querydsl.ResultRepositoryCustom;
import gutsandgun.kite_result.entity.read.*;
import gutsandgun.kite_result.projection.ResultTxAvgLatencyProjection;
import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.projection.ResultTxTransferStatsProjection;
import gutsandgun.kite_result.repository.read.*;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		Map<Long, Broker> brokerMap = readBrokerRepository.findAllMap();


		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(userId, sendingId);
		List<ResultTx> resultTxList = readResultTxRepository.findByResultSendingId(resultSending.getId());
		List<ResultTxTransfer> resultTxTransferList = resultTxTransferRepository.findByTxIdIn(resultTxList.stream().map(ResultTx::getId).collect(Collectors.toList()));
		List<ResultTxTransferStatsProjection> txTransferAvgLatencyGroupByBrokerId = resultTxTransferRepository.getTxTransferAvgLatencyGroupByBrokerId(resultTxList.stream().map(ResultTx::getId).collect(Collectors.toList()));


		NameDateListDto brokerCount =
				new NameDateListDto(
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getName()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getColor()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection::getCount).collect(Collectors.toList())
				);

		NameDateListDto brokerSpeed =
				new NameDateListDto(
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getName()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getColor()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection::getAvgLatency).collect(Collectors.toList())
				);



		//여기 존나 심각 나중에 다시 보기
		Map<Long, Map<Boolean, Long>> brokerSuccessFail;
		brokerSuccessFail = resultTxTransferList.stream()
				.collect(Collectors.groupingBy(resultTxTransfer -> resultTxTransfer.getBrokerId(),
						Collectors.groupingBy(resultTxTransfer -> resultTxTransfer.getSuccess(), Collectors.counting())));


		List<String> tempName = new ArrayList<>();
		List<String> tempColor = new ArrayList<>();
		List<Long> tempData = new ArrayList<>();

		for (Long key : brokerSuccessFail.keySet().stream().toList()) {
			Map<Boolean, Long> tempCnt = brokerSuccessFail.get(key);
			tempName.add(brokerMap.get(key).getName() + "- 성공");
			tempColor.add(brokerMap.get(key).getColor());
			if (tempCnt.containsKey(Boolean.TRUE))
				tempData.add(tempCnt.get(Boolean.TRUE));
			else
				tempData.add(0L);

			tempName.add(brokerMap.get(key).getName() + "- 실패");
			tempColor.add(brokerMap.get(key).getColor());
			if (tempCnt.containsKey(Boolean.FALSE))
				tempData.add(tempCnt.get(Boolean.FALSE));
			else
				tempData.add(0L);
		}

		NameDateListDto temp = new NameDateListDto(tempName, tempColor, tempData);


		ResultBrokerDto resultBrokerDto = new ResultBrokerDto(sendingId, brokerCount, temp, brokerSpeed);
		return resultBrokerDto;
	}

	public Page<ResultTxDto> getResultSendingTx(String userId, Pageable pageable, Long sendingId) {
		Page<ResultTx> resultTxPage = readResultTxRepository.findByUserIdAndResultSendingId(userId, findResultSendingId(sendingId), pageable);
		Page<ResultTxDto> resultTxDtoPage = resultTxPage.map(ResultTxDto::toDto);
		return resultTxDtoPage;
	}


	public Page<ResultSendingDto> getFilteredResultSendingList(String userId, SendingType sendingType, String startDt, String endDt, SendingStatus sendingStatus, PageRequestDTO pageRequestDTO) throws ParseException {

		Pageable pageable = pageRequestDTO.getPageable(Sort.by("reg_dt").descending());

		Page<ResultSendingDto> tuplePageList = resultRepositoryCustom.findByRegIdAndSendingTypeAndSuccessAndRegDt(userId, sendingType, startDt, endDt, sendingStatus, pageable);
		List<ResultSendingDto> list = tuplePageList.getContent();
		return new PageImpl<>(list, pageable, tuplePageList.getTotalElements());
	}
  

	public ResultTxDetailDto getResultSendingTxDetail(String userId, Long sendingId, Long txId) {
		Long resultSendingId = findResultSendingId(sendingId);
		ResultTx resultTx = readResultTxRepository.findByUserIdAndResultSendingIdAndTxId(userId, resultSendingId, txId);
		List<ResultTxTransferDto> resultTxTransferList = resultTxTransferRepository.findByTxId(txId);

		return ResultTxDetailDto.toDto(resultTx, resultTxTransferList);
	}
}
