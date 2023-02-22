package gutsandgun.kite_result.service;

import gutsandgun.kite_result.dto.ResultTxSuccessDto;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.ResultTx;
import gutsandgun.kite_result.entity.read.ResultTxTransfer;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.projection.ResultTxAvgLatencyProjection;
import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.publisher.RabbitMQProducer;
import gutsandgun.kite_result.repository.read.ReadResultSendingRepository;
import gutsandgun.kite_result.repository.read.ReadResultTxRepository;
import gutsandgun.kite_result.repository.read.ResultTxTransferRepository;
import gutsandgun.kite_result.type.SendingStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class SendingStatusService {

	private final RabbitMQProducer rabbitMQProducer;
	private final ReadResultSendingRepository resultSendingRepository;
	private final ReadResultTxRepository resultTxRepository;
	private final ResultTxTransferRepository resultTxTransferRepository;


	Map<Long, ResultTxSuccessDto> getSuccessCntMap(List<Long> sendingIdList) {

		Map<Long, List<ResultTxSuccessRateProjection>> successCountProjectionMap =
				resultSendingRepository.getTxSuccessCountGroupByResultSendingByUserIdAndSendingId(sendingIdList)
						.stream()
						.collect(Collectors.groupingBy(ResultTxSuccessRateProjection::getSendingId));

		Map<Long, ResultTxSuccessDto> successDtoMap = sendingIdList
				.stream()
				.collect(Collectors.toMap(sendingId -> sendingId, sendingId -> {
					if (successCountProjectionMap.containsKey(sendingId))
						return new ResultTxSuccessDto(sendingId, successCountProjectionMap.get(sendingId));
					else
						return new ResultTxSuccessDto(sendingId, 0, 0);
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
	Map<Long, Long> getLatencyAvgMap(List<Long> sendingIdList) {

		Map<Long, Long> txLatencyAvgProjectionMap
				= resultTxTransferRepository.getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(sendingIdList)
				.stream()
				.collect(Collectors.toMap(ResultTxAvgLatencyProjection::getSendingId,
						resultTxAvgLatencyProjection -> {
							if (resultTxAvgLatencyProjection.getAvgLatency() != null)
								return resultTxAvgLatencyProjection.getAvgLatency();
							else
								return 0L;
						}));

		Map<Long, Long> txLatencyAvgMap = sendingIdList
				.stream()
				.collect(Collectors.toMap(sendingId -> sendingId, sendingId ->
						txLatencyAvgProjectionMap.getOrDefault(sendingId, 0L)));


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
					return txLatencyAvgProjectionMap.getOrDefault(key, 0L);
				}));


		return txLatencyAvgMap;
	}


	Boolean checkSendingCompleteNotChecked(ResultSending resultSending) {
		List<SendingStatus> sendingStatusList = Arrays.asList(SendingStatus.COMPLETE, SendingStatus.FAIL);
		if (resultSending.getSendingStatus() == SendingStatus.SENDING) {
			return resultSending.getTotalMessage() == resultTxRepository.countByResultSendingIdAndStatusIn(resultSending.getId(), sendingStatusList);
		} else
			return false;
	}

	ResultSending getCurrentSendingResultStatus(Sending sending, ResultSending resultSending) {

		System.out.println("check sending id : " + sending.getId());
		boolean completeFlag = checkSendingCompleteNotChecked(resultSending);

		List<ResultTx> resultTxList = resultTxRepository.findByResultSendingId(resultSending.getId());
		List<Long> resultTxIdList = resultTxList.stream().map(ResultTx::getId).collect(Collectors.toList());

		Long failCnt = getSuccessCntMap(singletonList(sending.getId())).get(sending.getId()).getFailCnt();
		resultSending.setFailedMessage(failCnt);


		if (Objects.equals(failCnt, resultSending.getTotalMessage())) {
			resultSending.setSuccess(Boolean.FALSE);
			resultSending.setSendingStatus(SendingStatus.FAIL);
		} else {
			resultSending.setSuccess(Boolean.TRUE);
		}


//		Long avgLatency = getLatencyAvgMap(singletonList(sending.getId())).get(sending.getId());
		Long avgLatency = getQueLatencyAvgMap(singletonList(sending.getId())).get(sending.getId());
		resultSending.setAvgLatency(Float.valueOf(avgLatency));


		long completeTime = resultTxTransferRepository.findFirstByResultTxIdInOrderByCompleteTimeDesc(resultTxIdList)
				.map(ResultTxTransfer::getCompleteTime)
				.orElse(0L);

		resultSending.setCompleteTime(completeTime);


		if (completeFlag) {
			if (resultSending.getSuccess())
				resultSending.setSendingStatus(SendingStatus.COMPLETE);
			else
				resultSending.setSendingStatus(SendingStatus.FAIL);

			rabbitMQProducer.logSendQueue("Service: Result, type: " + resultSending.getSendingStatus().toString().toUpperCase() + ", resultSendingId: " + resultSending.getId() + ", success: " + resultSending.getSuccess().toString() + ", failedMessage: " + resultSending.getFailedMessage() + ", avgLatency: " + resultSending.getAvgLatency() + ", completeTime: " + resultSending.getCompleteTime() + ", time: " + new Date().getTime() + "@");
			System.out.println("Service: Result, type: " + resultSending.getSendingStatus().toString().toUpperCase() + ", resultSendingId: " + resultSending.getId() + ", success: " + resultSending.getSuccess().toString() + ", failedMessage: " + resultSending.getFailedMessage() + ", avgLatency: " + resultSending.getAvgLatency() + ", completeTime: " + resultSending.getCompleteTime() + ", time: " + new Date().getTime() + "@");
		}

		return resultSending;
	}

	//result 조회할때 한번씩 체크하도록 반영하기
	public Map<Long, ResultSending> findResultSendingInSendingIdList(List<Sending> sendingList) {

		List<Long> sendingIdList = sendingList.stream().map(Sending::getId).collect(Collectors.toList());
		Map<Long, ResultSending> resultSendingMap = resultSendingRepository.findBySendingIdIn(sendingIdList)
				.stream()
				.collect(Collectors.toMap(ResultSending::getSendingId, Function.identity(), (p1, p2) -> p1));

		for (Sending sending : sendingList) {
			ResultSending resultSending = resultSendingMap.getOrDefault(sending.getId(), new ResultSending());

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
}
