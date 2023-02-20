package gutsandgun.kite_result.dto;

import com.querydsl.core.annotations.QueryProjection;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.type.SendingRuleType;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.ResultSending} entity
 */
@Data
@Builder
public class ResultSendingDto implements Serializable {
	private final Long id;
	private final String userId;
	private final Long sendingId;
	private final SendingType sendingType;
	private final SendingRuleType sendingRuleType;
	private final Boolean success;
	private final Long totalMessage;
	private final Long failedMessage;
	private final float avgLatency;

	private final Long inputTime;
	private final Long scheduleTime;
	private final Long startTime;
	private final Long completeTime;
	private final Long logTime;
	private final SendingStatus sendingStatus;

	private final ResultTxSuccessDto resultTxSuccessDto;


	@QueryProjection
	public ResultSendingDto(Long id, String userId, Long sendingId, SendingType sendingType, SendingRuleType sendingRuleType, Boolean success,
							Long totalMessage, Long failedMessage, float avgLatency, Long inputTime, Long scheduleTime, Long startTime,
							Long completeTime, Long logTime, SendingStatus sendingStatus, ResultTxSuccessDto resultTxSuccessDto) {

		this.id = id;
		this.userId = userId;
		this.sendingId = sendingId;
		this.sendingType = sendingType;
		this.sendingRuleType = sendingRuleType;
		this.success = success;
		this.totalMessage = totalMessage;
		this.failedMessage = failedMessage;
		this.avgLatency = avgLatency;
		this.inputTime = inputTime;
		this.scheduleTime = scheduleTime;
		this.startTime = startTime;
		this.completeTime = completeTime;
		this.logTime = logTime;
		this.sendingStatus = sendingStatus;
		this.resultTxSuccessDto = resultTxSuccessDto;
	}

	public static ResultSendingDto toDto(Sending sending, ResultSending resultSending, ResultTxSuccessDto resultTxSuccessDto) {
		if (resultTxSuccessDto == null) {
			resultTxSuccessDto = new ResultTxSuccessDto(resultSending.getSendingId(), 0, 0);
		}
		switch (resultSending.getSendingStatus()) {
			case PENDING, FAIL, DELAY:
				resultSending.setAvgLatency(0F);
				break;
		}


		return ResultSendingDto.builder()
				.id(sending.getId())
				.userId(sending.getUserId())
				.sendingId(sending.getId())
				.sendingType(sending.getSendingType())
				.sendingRuleType(sending.getSendingRuleType())
				.success(resultSending.getSuccess())
				.totalMessage(sending.getTotalMessage())
				.failedMessage(resultSending.getFailedMessage())
				.avgLatency(resultSending.getAvgLatency())
				.inputTime(sending.getInputTime())
				.scheduleTime(sending.getScheduleTime())
				.startTime(resultSending.getStartTime())
				.completeTime(resultSending.getCompleteTime())
				.logTime(resultSending.getLogTime())
				.sendingStatus(resultSending.getSendingStatus())
				.resultTxSuccessDto(resultTxSuccessDto)
				.build();
	}
}
