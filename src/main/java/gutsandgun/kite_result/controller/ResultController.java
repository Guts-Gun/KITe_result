package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.LogSendingDto;
import gutsandgun.kite_result.dto.SendingDto;
import gutsandgun.kite_result.dto.UsageDto;
import gutsandgun.kite_result.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/result")
@AllArgsConstructor
public class ResultController {
	private final ResultService resultService;

	@GetMapping("/usage")
	public List<UsageDto> getTotalUsage(){
		List<UsageDto> usageDtoList = resultService.getTotalUsage();
	return(usageDtoList);
	}

	@GetMapping("/sending")
	public List<SendingDto> getTotalSending(){
		List<SendingDto> sendingDtoList = resultService.getTotalSending();
		return (sendingDtoList);
	}

	@GetMapping("/sending/result")
	public List<LogSendingDto> getTotalSendingLog(){
		List<LogSendingDto> sendingDtoList = resultService.getTotalSendingLog();

		return sendingDtoList;
	}
}
