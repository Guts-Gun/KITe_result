package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.LogSendingDto;
import gutsandgun.kite_result.dto.SendingDto;
import gutsandgun.kite_result.dto.UsageDto;
import gutsandgun.kite_result.entity.read.LogSending;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.repository.read.ReadLogSendingRepository;
import gutsandgun.kite_result.repository.read.ReadSendingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class ResultService {
	private final ReadSendingRepository readSendingRepository;
	private final ReadLogSendingRepository readLogSendingRepository;

	Long findUser() {
		return (1L);
	}
	public List<UsageDto> getTotalUsage() {

		//여기 심각함 나중에 고치기
		List<UsageDto> usageDtoList = new java.util.ArrayList<>(emptyList());
		List<Sending> sendingList = readSendingRepository.findByUserId(1L);

		Map<String, LongSummaryStatistics> testCollect = sendingList.stream()
				.collect(Collectors.groupingBy(Sending::getSendingType, Collectors.summarizingLong(Sending::getTotalSending))
				);
		System.out.println(testCollect.toString());

		for (Map.Entry<String, LongSummaryStatistics> entry : testCollect.entrySet()) {
			String s = entry.getKey();
			LongSummaryStatistics longSummaryStatistics = entry.getValue();
			usageDtoList.add(new UsageDto(s, 1000L, longSummaryStatistics.getSum()));
		}

		return usageDtoList;
	}


	public List<SendingDto> getTotalSending() {
		List<Sending> sendingList = readSendingRepository.findByUserId(1L);
		List<SendingDto> sendingDtoList = sendingList.stream().map(sending -> {
			return new SendingDto(sending);
		}).collect(Collectors.toList());
		System.out.println(sendingDtoList.toString());

		return sendingDtoList;

	}

	public List<LogSendingDto> getTotalSendingLog() {
		//여기 종료후 로그에서만 읽어오는데 종료 안된거도 읽어오게 할지 고민하기
		List<LogSending>  logSendingList = readLogSendingRepository.findByUserId(findUser());
		List<LogSendingDto>  logSendingDtoList = logSendingList.stream().map(logSending -> {
			return new LogSendingDto(logSending);
		} ).collect(Collectors.toList());
		System.out.println(logSendingDtoList);
		return logSendingDtoList;
	}
	public LogSendingDto getSendingLog(Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		LogSending logSending = readLogSendingRepository.findByUserIdAndSendingId(findUser(), sendingId);
		LogSendingDto logSendingDto = new LogSendingDto(logSending);
		System.out.println(logSendingDto);
		return logSendingDto;
	}


}