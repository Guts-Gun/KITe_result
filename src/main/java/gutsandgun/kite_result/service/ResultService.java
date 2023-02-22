package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.entity.read.*;
import gutsandgun.kite_result.exception.CustomException;
import gutsandgun.kite_result.exception.ErrorCode;
import gutsandgun.kite_result.projection.ResultTxTransferStatsProjection;
import gutsandgun.kite_result.querydsl.ResultRepositoryCustom;
import gutsandgun.kite_result.repository.read.*;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@Slf4j
@AllArgsConstructor
public class ResultService {

	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository resultSendingRepository;
	private final ReadResultTxRepository resultTxRepository;
	private final ReadBrokerRepository readBrokerRepository;
	private final ResultTxTransferRepository resultTxTransferRepository;
	private final ReadSendingMsgRepository sendingMsgRepository;

	private final ResultRepositoryCustom resultRepositoryCustom;

	private final SendingStatusService sendingStatusService;

	Long getUsageCap(String userId) {
		return 100L;
	}

	ResultSending findResultSendingId(Long sendingId) {
		return resultSendingRepository.findBySendingId(sendingId).orElseThrow(() -> new CustomException(ErrorCode.SENDING_NOT_FOUND));
	}

	public List<TotalUsageDto> getTotalUsage(String userId) {

		List<TotalUsageDto> totalUsageDtoList = resultSendingRepository.findTotalUsageBySendingTypeAndUserId(userId)
				.stream()
				.map(totalUsage -> new TotalUsageDto(totalUsage.getSendingType(), totalUsage.getTotalUsage(), getUsageCap(userId)))
				.collect(Collectors.toList());
		if (totalUsageDtoList.size() == 0) {
			totalUsageDtoList.add(new TotalUsageDto(SendingType.SMS, 0L, getUsageCap(userId)));
		}
		totalUsageDtoList.add(new TotalUsageDto(SendingType.MMS, 0L, getUsageCap(userId)));

		return totalUsageDtoList;
	}


	public List<SendingShortInfoDto> getTotalSendingShortInfo(String userId) {

		List<Sending> sendingList = readSendingRepository.findByUserIdOrderByInputTimeDesc(userId);
		if (sendingList.size() == 0)
			return new ArrayList<>();

		Map<Long, ResultSending> resultSendingMap = sendingStatusService.findResultSendingInSendingIdList(sendingList);

		Map<Long, ResultTxSuccessDto> successDtoMap = sendingStatusService.getSuccessCntMap(sendingList.stream().map(Sending::getId).toList());


		List<SendingShortInfoDto> sendingShortInfoDtoList = sendingList.stream()
				.map(sending -> {
//					try {
					return new SendingShortInfoDto(sending, resultSendingMap.getOrDefault(sending.getId(), new ResultSending()), successDtoMap.getOrDefault(sending.getId(), new ResultTxSuccessDto(0L, 0, 0)));
//					} catch (NullPointerException e) {
//						return new SendingShortInfoDto(sending, resultSendingMap.get(sending.getId()), successDtoMap.get(sending.getId()));
//					}
				})
				.collect(Collectors.toList());

		return sendingShortInfoDtoList;

	}

	public Page<ResultSendingDto> getTotalResultSending(String userId, Pageable pageable) {

		Page<ResultSending> resultSendingPage = resultSendingRepository.findByUserId(userId, pageable);

		Map<Long, Sending> sendingMap = readSendingRepository.findByUserIdAndIdIn(userId, resultSendingPage.getContent().stream().map(ResultSending::getSendingId).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(Sending::getId, Function.identity()));
		Map<Long, ResultTxSuccessDto> successDtoMap = sendingStatusService.getSuccessCntMap(resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Map<Long, Long> txLatencyAvgMap = sendingStatusService.getLatencyAvgMap(resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Page<ResultSendingDto> resultSendingDtoList = resultSendingPage.map(resultSending -> ResultSendingDto.toDto(sendingMap.get(resultSending.getSendingId()), resultSending, successDtoMap.get(resultSending.getSendingId())));
		System.out.println(resultSendingDtoList);
		return resultSendingDtoList;
	}


	public ResultSendingDto getResultSending(String userId, Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		Sending sending = readSendingRepository.findByIdAndUserId(sendingId, userId).orElseThrow(() -> new CustomException(ErrorCode.SENDING_NOT_FOUND));
		ResultSending resultSending = sendingStatusService.findResultSendingInSendingIdList(singletonList(sending)).get(sending.getId());
//				.orElseThrow( /*new CustomException(ErrorCode.RESULT_SENDING_NOT_FOUND*/);


		Map<Long, ResultTxSuccessDto> successDtoMap = sendingStatusService.getSuccessCntMap(singletonList(resultSending.getSendingId()));


		ResultSendingDto resultSendingDto = ResultSendingDto.toDto(sending, resultSending, successDtoMap.get(resultSending.getSendingId()));
//		System.out.println(resultSendingDto);
		return resultSendingDto;
	}


	public ResultBrokerDto getResultSendingBroker(String userId, Long sendingId) {
		Map<Long, Broker> brokerMap = readBrokerRepository.findAllMap();


		ResultSending resultSending = resultSendingRepository.findByUserIdAndSendingId(userId, sendingId).orElse(new ResultSending());
//				.orElseThrow(() -> new CustomException(ErrorCode.SENDING_NOT_FOUND));
		List<ResultTx> resultTxList = resultTxRepository.findByResultSendingId(resultSending.getId());
		List<ResultTxTransfer> resultTxTransferList = resultTxTransferRepository.findByResultTxIdIn(resultTxList.stream().map(ResultTx::getId).collect(Collectors.toList()));
		List<ResultTxTransferStatsProjection> txTransferAvgLatencyGroupByBrokerId = resultTxTransferRepository.getTxTransferAvgLatencyGroupByBrokerId(resultTxList.stream().map(ResultTx::getId).collect(Collectors.toList()));


		NameDateListDto brokerCount =
				new NameDateListDto(
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getName()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getColor()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection::getCount).collect(Collectors.toList())
				);

		NameDateListDto brokerSpeed =
				new NameDateListDto(
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getName()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection -> brokerMap.get(ResultTxTransferStatsProjection.getBrokerId()).getColor()).collect(Collectors.toList()),
						txTransferAvgLatencyGroupByBrokerId.stream().map(ResultTxTransferStatsProjection::getAvgLatency).collect(Collectors.toList())
				);


		List<String> brokerNames = new ArrayList<>();
		List<String> brokerColors = new ArrayList<>();
		List<Long> brokerSuccessFailCounts = new ArrayList<>();

		try {
			//여기 존나 심각 나중에 다시 보기
			Map<Long, Map<Boolean, Long>> brokerSuccessFail;
			brokerSuccessFail = resultTxTransferList.stream()
					.collect(Collectors.groupingBy(resultTxTransfer -> resultTxTransfer.getBrokerId(),
							Collectors.groupingBy(resultTxTransfer -> resultTxTransfer.getSuccess(), Collectors.counting())));


			for (Long key : brokerSuccessFail.keySet().stream().toList()) {
				Map<Boolean, Long> tempCnt = brokerSuccessFail.get(key);
				brokerNames.add(brokerMap.get(key).getName() + "- 성공");
				brokerColors.add(brokerMap.get(key).getColor());
				brokerSuccessFailCounts.add(tempCnt.getOrDefault(Boolean.TRUE, 0L));

				brokerNames.add(brokerMap.get(key).getName() + "- 실패");
				brokerColors.add(brokerMap.get(key).getColor());
				brokerSuccessFailCounts.add(tempCnt.getOrDefault(Boolean.FALSE, 0L));
			}
		} catch (NullPointerException e) {
			System.out.println("resultTxTransferList Missing Can't get Broker S/F Cnt Sending id : " + sendingId);
		}

		NameDateListDto brokerSuccessFailDto = new NameDateListDto(brokerNames, brokerColors, brokerSuccessFailCounts);


		ResultBrokerDto resultBrokerDto = new ResultBrokerDto(sendingId, brokerCount, brokerSuccessFailDto, brokerSpeed);
		return resultBrokerDto;
	}

	public Page<ResultTxDto> getResultSendingTx(String userId, Pageable pageable, Long sendingId) {
		Map<Long, Broker> brokerMap = readBrokerRepository.findAllMap();

		Page<SendingMsg> sendingMsgPage = sendingMsgRepository.findBySendingId(sendingId, pageable);
		Map<Long, ResultTx> resultTxMap = resultTxRepository.findByUserIdAndTxIdIn(userId, sendingMsgPage.stream().map(SendingMsg::getId).collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(ResultTx::getTxId, Function.identity()));

		Page<ResultTxDto> resultTxDtoPage = Page.empty();
		try {
			resultTxDtoPage = sendingMsgPage.map(SendingMsg -> ResultTxDto.toDto(SendingMsg, resultTxMap.get(SendingMsg.getId()), brokerMap.get(resultTxMap.get(SendingMsg.getId()).getBrokerId()).getName()));
		} catch (Exception e) {
			System.out.println("getResultSendingTx null exception Sending Id : " + sendingId);
		}
		return resultTxDtoPage;
	}


	public Page<ResultSendingDto> getFilteredResultSendingList(String userId, SendingType sendingType, String startDt, String endDt, SendingStatus sendingStatus, PageRequestDTO pageRequestDTO) throws ParseException {

		Pageable pageable = pageRequestDTO.getPageable(Sort.by("reg_dt").descending());

		Page<ResultSendingDto> tuplePageList = resultRepositoryCustom.findByRegIdAndSendingTypeAndSuccessAndRegDt(userId, sendingType, startDt, endDt, sendingStatus, pageable);
		List<ResultSendingDto> list = tuplePageList.getContent();
		return new PageImpl<>(list, pageable, tuplePageList.getTotalElements());
	}


	public ResultTxDetailDto getResultSendingTxDetail(String userId, Long sendingId, Long txId) {
		Map<Long, Broker> brokerMap = readBrokerRepository.findAllMap();


		SendingMsg sendingMsg = sendingMsgRepository.findById(txId).orElseThrow(() -> new CustomException(ErrorCode.RESULT_SENDING_NOT_FOUND));
		ResultSending resultSending = findResultSendingId(sendingId);
		ResultTx resultTx = resultTxRepository.findByUserIdAndResultSendingIdAndTxId(userId, resultSending.getId(), txId);

		List<ResultTxTransferDto> resultTxTransferList = resultTxTransferRepository.findByResultTxIdOrderByCompleteTimeDesc(resultTx.getId());
		resultTxTransferList.forEach(item -> item.setBrokerName(brokerMap.get(item.getBrokerId()).getName()));

		String brokerName = readBrokerRepository.findById(resultTx.getBrokerId()).get().getName();

		Long sendTime = 0L;
		try {
			sendTime = resultTxTransferList.stream().min(Comparator.comparing(ResultTxTransferDto::getSendTime)).get().getSendTime();
		} catch (Exception e) {
			log.info("sendingId: " + sendingId + ", txId : " + txId + ", result Transfer is missing ");
			sendTime = 0L;
		}

		Long completeTime;
		try {
			completeTime = resultTxTransferList.stream().max(Comparator.comparing(ResultTxTransferDto::getCompleteTime)).get().getCompleteTime();
		} catch (Exception e) {
			log.info("sendingId: " + sendingId + ", txId : " + txId + ", result Transfer is missing ");
			completeTime = 0L;
		}

		return ResultTxDetailDto.toDto(sendingMsg, resultTx, resultTxTransferList, sendTime, completeTime, brokerName);
	}
}
