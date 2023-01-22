package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.ResultSendingDto;
import gutsandgun.kite_result.dto.SendingDto;
import gutsandgun.kite_result.dto.UsageDto;
import gutsandgun.kite_result.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;
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
	public Page<ResultSendingDto> getTotalResultSending(Pageable pageable){
		return resultService.getTotalResultSending(pageable);
	}

	@GetMapping("/sending/result/{sendingId}")
	public LogSendingDto getSendingLog(@PathVariable Long sendingId){
		LogSendingDto logSendingDto = resultService.getSendingLog(sendingId);
		return logSendingDto;
	}
}
