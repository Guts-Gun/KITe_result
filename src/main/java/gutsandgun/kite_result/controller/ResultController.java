package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.*;
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
	public ResultSendingDto getResultSending(@PathVariable Long sendingId){
		ResultSendingDto resultSendingDto = resultService.getResultSending(sendingId);
		return resultSendingDto;
	}

	@GetMapping("/sending/result/{sendingId}/broker")
	public ResultBrokerDto getResultSendingBroker(@PathVariable Long sendingId) {
		ResultBrokerDto resultBrokerDto = resultService.getResultSendingBroker(sendingId);
		return resultBrokerDto;
	}

	@GetMapping("/sending/result/{sendingId}/tx")
	public Page<ResultTxDto> getResultSendingTx(Pageable pageable, @PathVariable Long sendingId) {
		return resultService.getResultSendingTx(pageable, sendingId);
	}

}
