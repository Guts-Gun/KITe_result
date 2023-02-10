package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.entity.read.Broker;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.ResultTx;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.repository.read.ReadBrokerRepository;
import gutsandgun.kite_result.repository.read.ReadResultSendingRepository;
import gutsandgun.kite_result.repository.read.ReadResultTxRepository;
import gutsandgun.kite_result.repository.read.ReadSendingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@Service
@AllArgsConstructor
public class ResultService {
	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository resultSendingRepository;
	private final ReadResultTxRepository readResultTxRepository;
	private final ReadBrokerRepository readBrokerRepository;


	String findUser(Principal principal) {
//		JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
//		String userId = token.getTokenAttributes().get("preferred_username").toString();
//		System.out.println(userId);
		// 여긴 나중에
//		return(userId);
		return ("solbitest");
	}

	Long getUsageCap(String userId) {
		return 100L;
	}

	Long findResultSendingId(Long sendingId) {
		return resultSendingRepository.findBySendingId(sendingId).getId();
	}

	public List<TotalUsageDto> getTotalUsage(String userId) {

		List<TotalUsageDto> totalUsageDtoList = resultSendingRepository.findTotalUsageBySendingTypeAndUserId(userId)
				.stream()
				.map(totalUsage -> new TotalUsageDto(totalUsage.getSendingType(), totalUsage.getTotalUsage(), getUsageCap(userId)))
				.collect(Collectors.toList());

		return totalUsageDtoList;
	}


	public List<SendingShortInfoDto> getTotalSending(String userId) {

		List<Sending> sendingList = readSendingRepository.findByUserId(userId);

		List<ResultTxSuccessRateProjection> projectionList = readResultTxRepository.getTxSuccessCountGroupByResultSendingByUserId(userId);
		List<ResultTxSuccessDto> resultTxSuccessDtoList = projectionList.stream()
				.map(item -> new ResultTxSuccessDto(item.getSendingId(), item.getSuccess(), item.getCount()))
				.toList();

		List<SendingShortInfoDto> sendingShortInfoDtoList = sendingList
				.stream()
				.map(sending -> new SendingShortInfoDto(sending,
						resultTxSuccessDtoList.stream()
								.filter(item -> item.getSendingId()
										.equals(sending.getId()))
								.collect(Collectors.toList())))
				.collect(Collectors.toList());

		return sendingShortInfoDtoList;

	}

	public Page<ResultSendingDto> getTotalResultSending(Principal principal, Pageable pageable) {
		Page<ResultSending> resultSendingPage = resultSendingRepository.findByUserId(findUser(principal), pageable);
		Page<ResultSendingDto> resultSendingDtoList = resultSendingPage.map(ResultSendingDto::toDto);
		System.out.println(resultSendingDtoList);
		return resultSendingDtoList;
	}

	public ResultSendingDto getResultSending(Principal principal, Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(findUser(principal), sendingId);
		ResultSendingDto resultSendingDto = ResultSendingDto.toDto(resultSending);
		System.out.println(resultSendingDto);
		return resultSendingDto;
	}


	public ResultBrokerDto getResultSendingBroker(Principal principal, Long sendingId) {
		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(findUser(principal), sendingId);
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

	public Page<ResultTxDto> getResultSendingTx(Principal principal, Pageable pageable, Long sendingId) {
		Page<ResultTx> resultTxPage = readResultTxRepository.findByUserIdAndResultSendingId(findUser(principal), findResultSendingId(sendingId), pageable);
		Page<ResultTxDto> resultTxDtoPage = resultTxPage.map(ResultTxDto::toDto);
		return resultTxDtoPage;
	}
}
