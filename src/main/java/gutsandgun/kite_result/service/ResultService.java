package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.*;
import gutsandgun.kite_result.entity.read.*;
import gutsandgun.kite_result.exception.CustomException;
import gutsandgun.kite_result.exception.ErrorCode;
import gutsandgun.kite_result.projection.ResultTxAvgLatencyProjection;
import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.projection.ResultTxTransferStatsProjection;
import gutsandgun.kite_result.publisher.RabbitMQProducer;
import gutsandgun.kite_result.querydsl.ResultRepositoryCustom;
import gutsandgun.kite_result.repository.read.*;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class ResultService {
	private final RabbitMQProducer rabbitMQProducer;
	private final ReadSendingRepository readSendingRepository;
	private final ReadResultSendingRepository resultSendingRepository;
	private final ReadResultTxRepository resultTxRepository;
	private final ReadBrokerRepository readBrokerRepository;
	private final ResultTxTransferRepository resultTxTransferRepository;
	private final ReadSendingMsgRepository sendingMsgRepository;

	private final ResultRepositoryCustom resultRepositoryCustom;

	Long getUsageCap(String userId) {
		return 100L;
	}

	ResultSending findResultSendingId(Long sendingId) {
		return resultSendingRepository.findBySendingId(sendingId).orElseThrow(() -> new CustomException(ErrorCode.SENDING_NOT_FOUND));
	}

	Map<Long, ResultTxSuccessDto> getSuccessCntMap(List<Long> sendingId) {

		Map<Long, List<ResultTxSuccessRateProjection>> successCountProjectionMap =
				resultSendingRepository.getTxSuccessCountGroupByResultSendingByUserIdAndSendingId(sendingId)
						.stream()
						.collect(Collectors.groupingBy(ResultTxSuccessRateProjection::getSendingId));

		Map<Long, ResultTxSuccessDto> successDtoMap = sendingId
				.stream()
				.collect(Collectors.toMap(key -> key, key -> {
					if (successCountProjectionMap.containsKey(key))
						return new ResultTxSuccessDto(key, successCountProjectionMap.get(key));
					else
						return new ResultTxSuccessDto(key, 0, 0);
				}));
		return successDtoMap;
	}

//	Map<Long, Long> getLatencyAvgMap(List<Long> sendingIds) {
//		List<ResultTxAvgLatencyProjection> latencyProjections = resultTxTransferRepository.getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(sendingIds);
//
//		return latencyProjections.stream()
//				.collect(Collectors.toMap(
//						ResultTxAvgLatencyProjection::getSendingId,
//						projection -> Optional.ofNullable(projection.getAvgLatency()).orElse(0L)
//				));
//	}


	// 나중에 이거로 바꿔두기
	Map<Long, Long> getLatencyAvgMap(List<Long> sendingId) {

		Map<Long, Long> txLatencyAvgProjectionMap
				= resultTxTransferRepository.getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(sendingId)
				.stream()
				.collect(Collectors.toMap(ResultTxAvgLatencyProjection::getSendingId,
						resultTxAvgLatencyProjection -> {
							if (resultTxAvgLatencyProjection.getAvgLatency() != null)
								return resultTxAvgLatencyProjection.getAvgLatency();
							else
								return 0L;
						}));

		Map<Long, Long> txLatencyAvgMap = sendingId
				.stream()
				.collect(Collectors.toMap(key -> key, key ->
				{
					if (txLatencyAvgProjectionMap.containsKey(key))
						return txLatencyAvgProjectionMap.get(key);
					else
						return 0L;
				}));


		return txLatencyAvgMap;
	}

	Map<Long, Long> getQueLatencyAvgMap(List<Long> sendingId) {

		Map<Long, Long> txLatencyAvgProjectionMap
				= resultTxTransferRepository.getTxAvgLatencyBtwStartAndQueGroupByResultSendingByUserIdAndSendingId(sendingId)
				.stream()
				.collect(Collectors.toMap(ResultTxAvgLatencyProjection::getSendingId,
						resultTxAvgLatencyProjection -> {
							if (resultTxAvgLatencyProjection.getAvgLatency() != null)
								return resultTxAvgLatencyProjection.getAvgLatency();
							else
								return 0L;
						}));

		Map<Long, Long> txLatencyAvgMap = sendingId
				.stream()
				.collect(Collectors.toMap(key -> key, key ->
				{
					if (txLatencyAvgProjectionMap.containsKey(key))
						return txLatencyAvgProjectionMap.get(key);
					else
						return 0L;
				}));


		return txLatencyAvgMap;
	}


	Boolean checkSendingCompleteNotChecked(ResultSending resultSending) {
		if (resultSending.getSendingStatus() == SendingStatus.SENDING)
			return resultSending.getTotalMessage() == resultTxRepository.countByResultSendingIdAndStatusNot(resultSending.getId(), SendingStatus.PENDING);
		else
			return false;
	}

	ResultSending getCurrentSendingResultStatus(Sending sending, ResultSending resultSending) {
//		ResultSending resultSending = findResultSendingId(sending.getId());
		System.out.println("check sending id : " + sending.getId());
		List<Long> resultTxIdList = resultTxRepository.findByResultSendingId(resultSending.getId()).stream().map(ResultTx::getId).collect(Collectors.toList());

		Long failCnt = getSuccessCntMap(singletonList(sending.getId())).get(sending.getId()).getFailCnt();
		resultSending.setFailedMessage(failCnt);


		if (failCnt == resultSending.getTotalMessage()) {
			resultSending.setSuccess(Boolean.FALSE);
			resultSending.setSendingStatus(SendingStatus.FAIL);
		} else {
			resultSending.setSuccess(Boolean.TRUE);
		}
//		Long avgLatency = getLatencyAvgMap(singletonList(sending.getId())).get(sending.getId());
		Long avgLatency = getQueLatencyAvgMap(singletonList(sending.getId())).get(sending.getId());
		resultSending.setAvgLatency(Float.valueOf(avgLatency));

		//여기 하기
//		long completeTime = resultTxTransferRepository.findFirstByResultTxIdInOrderByCompleteTimeDesc(resultTxIdList)
//				.orElseGet(() -> {
//					ResultTxTransfer resultTxTransfer = new ResultTxTransfer();
//					resultTxTransfer.setCompleteTime(0L);
//					return resultTxTransfer;
//				}).getCompleteTime();
//
//		resultSending.setCompleteTime(completeTime);
		long completeTime = resultTxTransferRepository.findFirstByResultTxIdInOrderByCompleteTimeDesc(resultTxIdList)
				.map(ResultTxTransfer::getCompleteTime)
				.orElse(0L);

		resultSending.setCompleteTime(completeTime);


		if (checkSendingCompleteNotChecked(resultSending)) {
			resultSending.setSendingStatus(SendingStatus.COMPLETE);
			rabbitMQProducer.logSendQueue("Service: Result, type: " + resultSending.getSendingStatus().toString().toUpperCase() + ", resultSendingId: " + resultSending.getId() + ", success: " + resultSending.getSuccess().toString() + ", failedMessage: " + resultSending.getFailedMessage() + ", avgLatency: " + resultSending.getAvgLatency() + ", completeTime: " + resultSending.getCompleteTime() + ", time: " + new Date().getTime() + "@");
//			System.out.println("Service: Result, type: " + resultSending.getSendingStatus().toString().toUpperCase() + ", resultSendingId: " + resultSending.getId() + ", success: " + resultSending.getSuccess().toString() + ", failedMessage: " + resultSending.getFailedMessage() + ", avgLatency: " + resultSending.getAvgLatency() + ", completeTime: " + resultSending.getCompleteTime() + ", time: " + new Date().getTime() + "@");
		}

		return resultSending;
	}

	//result 조회할때 한번씩 체크하도록 반영하기
	public Map<Long, ResultSending> findResultSendingInSendingIdList(List<Sending> sendingList) {

		Map<Long, ResultSending> resultSendingMap = resultSendingRepository.findBySendingIdIn(sendingList.stream().map(Sending::getId).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(ResultSending::getSendingId, Function.identity(), (p1, p2) -> p1));

		for (Sending sending : sendingList) {
			ResultSending resultSending = resultSendingMap.get(sending.getId());

			if (resultSending == null)
				resultSending = new ResultSending();
			switch (resultSending.getSendingStatus()) {
				case COMPLETE, PENDING, FAIL, DELAY:
					break;
				case SENDING:
					resultSending = getCurrentSendingResultStatus(sending, resultSending);
					resultSendingMap.put(resultSending.getSendingId(), resultSending);
					break;

			}
		}


		return resultSendingMap;
	}

	public List<TotalUsageDto> getTotalUsage(String userId) {

		List<TotalUsageDto> totalUsageDtoList = resultSendingRepository.findTotalUsageBySendingTypeAndUserId(userId)
				.stream()
				.map(totalUsage -> new TotalUsageDto(totalUsage.getSendingType(), totalUsage.getTotalUsage(), getUsageCap(userId)))
				.collect(Collectors.toList());
		if (totalUsageDtoList.size() == 0) {
			totalUsageDtoList.add(new TotalUsageDto(SendingType.SMS, 0L, getUsageCap(userId)));
		}

		return totalUsageDtoList;
	}


	public List<SendingShortInfoDto> getTotalSendingShortInfo(String userId) {

		List<Sending> sendingList = readSendingRepository.findByUserId(userId);
		if (sendingList.size() == 0)
			return new ArrayList<>();

		Map<Long, ResultSending> resultSendingMap = findResultSendingInSendingIdList(sendingList);

		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(sendingList.stream().map(Sending::getId).toList());


		List<SendingShortInfoDto> sendingShortInfoDtoList = sendingList.stream()
				.map(sending -> {
//					try {
						return new SendingShortInfoDto(sending, resultSendingMap.getOrDefault(sending.getId(),new ResultSending()), successDtoMap.getOrDefault(sending.getId(), new ResultTxSuccessDto(0L,0,0)));
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
		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Map<Long, Long> txLatencyAvgMap = getLatencyAvgMap(resultSendingPage.stream().map(ResultSending::getSendingId).toList());

		Page<ResultSendingDto> resultSendingDtoList = resultSendingPage.map(resultSending -> ResultSendingDto.toDto(sendingMap.get(resultSending.getSendingId()), resultSending, successDtoMap.get(resultSending.getSendingId())));
		System.out.println(resultSendingDtoList);
		return resultSendingDtoList;
	}


	public ResultSendingDto getResultSending(String userId, Long sendingId) {
		//없을때 에러 어케 처리할지 정하기
		Sending sending = readSendingRepository.findByIdAndUserId(sendingId, userId).orElseThrow(() -> new CustomException(ErrorCode.SENDING_NOT_FOUND));
		ResultSending resultSending = findResultSendingInSendingIdList(singletonList(sending)).get(sending.getId());
//				.orElseThrow( /*new CustomException(ErrorCode.RESULT_SENDING_NOT_FOUND*/);


		Map<Long, ResultTxSuccessDto> successDtoMap = getSuccessCntMap(singletonList(resultSending.getSendingId()));


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

		List<ResultTxTransferDto> resultTxTransferList = resultTxTransferRepository.findByResultTxId(resultTx.getId());
		resultTxTransferList.forEach(item -> item.setBrokerName(brokerMap.get(item.getBrokerId()).getName()));

		String brokerName = readBrokerRepository.findById(resultTx.getBrokerId()).get().getName();
		Long sendTime = resultTxTransferList.stream().min(Comparator.comparing(ResultTxTransferDto::getSendTime)).get().getSendTime();
		Long completeTime = resultTxTransferList.stream().max(Comparator.comparing(ResultTxTransferDto::getCompleteTime)).get().getCompleteTime();

		return ResultTxDetailDto.toDto(sendingMsg, resultTx, resultTxTransferList, sendTime, completeTime, brokerName);
	}
}
