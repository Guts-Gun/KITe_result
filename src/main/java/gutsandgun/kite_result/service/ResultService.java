package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.ResultSendingDto;
import gutsandgun.kite_result.dto.SendingDto;
import gutsandgun.kite_result.dto.UsageDto;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.repository.read.ReadResultSendingRepository;
import gutsandgun.kite_result.repository.read.ReadSendingRepository;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class ResultService {
	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository readResultSendingRepository;

	String findUser() {
		return ("1");
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
}
