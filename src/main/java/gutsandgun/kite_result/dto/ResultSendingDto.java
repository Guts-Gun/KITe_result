package gutsandgun.kite_result.dto;

import com.querydsl.core.annotations.QueryProjection;
import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.type.SendingRuleType;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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
	private final Long avgLatency;

	private final Long inputTime;
	private final Long scheduleTime;
	private final Long startTime;
	private final Long completeTime;
	private final Long logTime;
	private final SendingStatus sendingStatus;

	private final ResultTxSuccessDto resultTxSuccessDto;


	@QueryProjection
	public ResultSendingDto(Long id, String userId, Long sendingId, SendingType sendingType, SendingRuleType sendingRuleType, Boolean success,
							Long totalMessage, Long failedMessage,  Long avgLatency, Long inputTime,  Long scheduleTime,  Long startTime,
							Long completeTime,  Long logTime, SendingStatus sendingStatus, ResultTxSuccessDto resultTxSuccessDto){

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

	public static ResultSendingDto toDto(final ResultSending resultSending, ResultTxSuccessDto resultTxSuccessDto, Long avgLatency) {
		if (resultTxSuccessDto == null) {
			resultTxSuccessDto = new ResultTxSuccessDto(resultSending.getSendingId(), 0, 0);
		}
		if (avgLatency == null)
			avgLatency = 0L;

		return ResultSendingDto.builder()
				.id(resultSending.getId())
				.userId(resultSending.getUserId())
				.sendingId(resultSending.getSendingId())
				.sendingType(resultSending.getSendingType())
				.sendingRuleType(resultSending.getSendingRuleType())
				.success(resultSending.getSuccess())
				.totalMessage(resultSending.getTotalMessage())
				.failedMessage(resultSending.getFailedMessage())
				.avgLatency(avgLatency)
				.inputTime(resultSending.getInputTime())
				.scheduleTime(resultSending.getScheduleTime())
				.startTime(resultSending.getStartTime())
				.completeTime(resultSending.getCompleteTime())
				.logTime(resultSending.getLogTime())
				.sendingStatus(resultSending.getSendingStatus())
				.resultTxSuccessDto(resultTxSuccessDto)
				.build();
	}
}
