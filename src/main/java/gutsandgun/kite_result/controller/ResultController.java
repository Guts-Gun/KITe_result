package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/result")
@AllArgsConstructor
public class ResultController {
	private final ResultService resultService;

	String findUser(Principal principal) {
		JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
		String userId = token.getTokenAttributes().get("preferred_username").toString();
		System.out.println(userId);
//		 여긴 나중에
//		return(userId);
		return ("solbitest");
	}

	@GetMapping("/usage")
	public List<TotalUsageDto> getTotalUsage(Principal principal) {
		List<TotalUsageDto> totalUsageDtoList = resultService.getTotalUsage(findUser(principal));
		return (totalUsageDtoList);
	}

	//token null 해결하기
	@GetMapping("/sending")
	public List<SendingDto> getTotalSending(Principal principal) {
		List<SendingDto> sendingDtoList = resultService.getTotalSending(principal);
		return (sendingDtoList);
	}

	@GetMapping("/sending/result")
	public Page<ResultSendingDto> getTotalResultSending(Principal principal, Pageable pageable) {
		return resultService.getTotalResultSending(principal, pageable);
	}

	@GetMapping("/sending/result/{sendingId}")
	public ResultSendingDto getResultSending(Principal principal, @PathVariable Long sendingId) {
		ResultSendingDto resultSendingDto = resultService.getResultSending(principal, sendingId);
		return resultSendingDto;
	}

	@GetMapping("/sending/result/{sendingId}/broker")
	public ResultBrokerDto getResultSendingBroker(Principal principal, @PathVariable Long sendingId) {
		ResultBrokerDto resultBrokerDto = resultService.getResultSendingBroker(principal, sendingId);
		return resultBrokerDto;
	}

	@GetMapping("/sending/result/{sendingId}/tx")
	public Page<ResultTxDto> getResultSendingTx(Principal principal, Pageable pageable, @PathVariable Long sendingId) {
		return resultService.getResultSendingTx(principal, pageable, sendingId);
	}

}
