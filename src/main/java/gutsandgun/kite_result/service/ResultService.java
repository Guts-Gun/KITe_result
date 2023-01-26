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
import gutsandgun.kite_result.type.FailReason;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class ResultService {
	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository readResultSendingRepository;
	private final ReadResultTxRepository readResultTxRepository;
	private final ReadBrokerRepository readBrokerRepository;


	String findUser() {
		return ("solbitest");
	}

	Long findResultSendingId(Long sendingId) {
		return readResultSendingRepository.findBySendingId(sendingId).getId();
	}

	public List<UsageDto> getTotalUsage() {

		//여기 심각함 나중에 고치기
		List<UsageDto> usageDtoList = new java.util.ArrayList<>(emptyList());
		List<Sending> sendingList = readSendingRepository.findByUserId(findUser());

		Map<SendingType, LongSummaryStatistics> testCollect = sendingList.stream()
				.collect(Collectors.groupingBy(Sending::getSendingType, Collectors.summarizingLong(Sending::getTotalMessage))
				);
		System.out.println(testCollect.toString());

		for (Map.Entry<SendingType, LongSummaryStatistics> entry : testCollect.entrySet()) {
			SendingType s = entry.getKey();
			LongSummaryStatistics longSummaryStatistics = entry.getValue();
			usageDtoList.add(new UsageDto(s, 1000L, longSummaryStatistics.getSum()));
		}

		return usageDtoList;
	}


	public List<SendingDto> getTotalSending() {
		List<Sending> sendingList = readSendingRepository.findByUserId(findUser());
		List<SendingDto> sendingDtoList = sendingList.stream().map(sending -> {
			return new SendingDto(sending);
		}).collect(Collectors.toList());
		System.out.println(sendingDtoList.toString());

		return sendingDtoList;

	}

	public Page<ResultSendingDto> getTotalResultSending(Pageable pageable) {
		Page<ResultSending> resultSendingPage = readResultSendingRepository.findByUserId(findUser(), pageable);
		Page<ResultSendingDto> resultSendingDtoList = resultSendingPage.map(ResultSendingDto::toDto);
		System.out.println(resultSendingDtoList);
		return resultSendingDtoList;
	}

	public ResultSendingDto getResultSending(Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		ResultSending resultSending = readResultSendingRepository.findByUserIdAndSendingId(findUser(), sendingId);
		ResultSendingDto resultSendingDto = ResultSendingDto.toDto(resultSending);
		System.out.println(resultSendingDto);
		return resultSendingDto;
	}


	public ResultBrokerDto getResultSendingBroker(Long sendingId) {
		ResultSending resultSending = readResultSendingRepository.findByUserIdAndSendingId(findUser(), sendingId);
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
		for (String key : brokerSuccessFail.keySet())
		{
			System.out.println(key);
			for (Boolean key2 : brokerSuccessFail.get(key).keySet())
			{
				System.out.println(key2);
				tempName.add(key + "-" + key2.toString());
				tempData.add(brokerSuccessFail.get(key).get(key2));
			}
		}

		NameDateListDto temp = new NameDateListDto(tempName, tempData);


		brokerSpeed = resultTxList.stream()
				.collect(Collectors.groupingBy(ResultTx -> brokerMap.get(ResultTx.getBrokerId()).getName(),
						Collectors.summarizingLong(ResultTx -> ResultTx.getCompleteTime() - ResultTx.getSendTime())));

		ResultBrokerDto resultBrokerDto = new ResultBrokerDto(sendingId, brokerCount, temp, brokerSpeed);

		return resultBrokerDto;
	}

	public Page<ResultTxDto> getResultSendingTx(Pageable pageable, Long sendingId) {
		Page<ResultTx> resultTxPage = readResultTxRepository.findByUserIdAndResultSendingId(findUser(), findResultSendingId(sendingId), pageable);
		Page<ResultTxDto> resultTxDtoPage = resultTxPage.map(ResultTxDto::toDto);
		return resultTxDtoPage;
	}
}
