package gutsandgun.kite_result.controller;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.exception.CustomException;
import gutsandgun.kite_result.exception.ErrorCode;
import gutsandgun.kite_result.service.ResultService;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
//		if(token == null)
//			throw new CustomException(ErrorCode.USER_NOT_FOUND);
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


	@GetMapping("/filteredResultList")
	public Page<ResultSendingDto> getFilteredResultSendingList(
														Principal principal,
														@RequestParam(value = "sendingType", required = false) SendingType sendingType,
														@RequestParam(value = "startDt", required = false) String startDt,
														@RequestParam(value = "endDt", required = false) String endDt,
														@RequestParam(value = "sendingStatus", required = false) SendingStatus sendingStatus,
														PageRequestDTO pageRequestDTO) throws ParseException {
		return resultService.getFilteredResultSendingList(findUser(principal), sendingType, startDt, endDt,sendingStatus, pageRequestDTO);
	}


	@GetMapping("/sending/result/{sendingId}/tx/{txId}")
	public ResultTxDetailDto getResultSendingTx(Principal principal, @PathVariable Long sendingId, @PathVariable Long txId) {
		return resultService.getResultSendingTxDetail(findUser(principal), sendingId, txId);
	}

}
