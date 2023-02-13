package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.service.ResultService;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/result")
@AllArgsConstructor
public class ResultController {
	private final ResultService resultService;

	String findUser(Principal principal) {
//		JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
//		String userId = token.getTokenAttributes().get("preferred_username").toString();
//		System.out.println(userId);
//		 여긴 나중에
//		return(userId);
		return ("solbitest");
	}

	@GetMapping("/usage")
	public List<TotalUsageDto> getTotalUsage(Principal principal) {
		List<TotalUsageDto> totalUsageDtoList = resultService.getTotalUsage(findUser(principal));
		return (totalUsageDtoList);
	}

	@GetMapping("/sending")
	public List<SendingShortInfoDto> getTotalSendingShortInfo(Principal principal) {
		List<SendingShortInfoDto> sendingShortInfoDtoList = resultService.getTotalSendingShortInfo(findUser(principal));
		return (sendingShortInfoDtoList);
	}

	@GetMapping("/sending/result")
	public Page<ResultSendingDto> getTotalResultSending(Principal principal, Pageable pageable) {
		return resultService.getTotalResultSending(findUser(principal), pageable);
	}

	@GetMapping("/sending/result/{sendingId}")
	public ResultSendingDto getResultSending(Principal principal, @PathVariable Long sendingId) {
		ResultSendingDto resultSendingDto = resultService.getResultSending(findUser(principal), sendingId);
		return resultSendingDto;
	}

	@GetMapping("/sending/result/{sendingId}/broker")
	public ResultBrokerDto getResultSendingBroker(Principal principal, @PathVariable Long sendingId) {
		ResultBrokerDto resultBrokerDto = resultService.getResultSendingBroker(findUser(principal), sendingId);
		return resultBrokerDto;
	}

	@GetMapping("/sending/result/{sendingId}/tx")
	public Page<ResultTxDto> getResultSendingTx(Principal principal, Pageable pageable, @PathVariable Long sendingId) {
		return resultService.getResultSendingTx(findUser(principal), pageable, sendingId);
	}


	@GetMapping("/sending/filteredResultList")
	public Page<ResultSendingDto> getFilteredResultSendingList(
			Principal principal, SendingType sendingType, String startDt, String endDt, SendingStatus sendingStatus, Pageable pageable) throws ParseException {
		return resultService.getFilteredResultSendingList(findUser(principal), sendingType, startDt, endDt,sendingStatus, pageable);
	}


	@GetMapping("/sending/result/{sendingId}/tx/{txId}")
	public ResultTxDetailDto getResultSendingTx(Principal principal, @PathVariable Long sendingId, @PathVariable Long txId) {
		return resultService.getResultSendingTxDetail(findUser(principal), sendingId, txId);
	}

}
