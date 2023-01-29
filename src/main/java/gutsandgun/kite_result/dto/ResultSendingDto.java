package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.ResultSending;
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
	private final Float avgSpeed;

	private final Long inputTime;
	private final Long scheduleTime;
	private final Long startTime;
	private final Long completeTime;
	private final Long logTime;
	private final SendingStatus sendingStatus;

	public static ResultSendingDto toDto(final ResultSending resultSending) {
		return ResultSendingDto.builder()
				.id(resultSending.getId())
				.userId(resultSending.getUserId())
				.sendingId(resultSending.getSendingId())
				.sendingType(resultSending.getSendingType())
				.sendingRuleType(resultSending.getSendingRuleType())
				.success(resultSending.getSuccess())
				.totalMessage(resultSending.getTotalMessage())
				.failedMessage(resultSending.getFailedMessage())
				.avgSpeed(resultSending.getAvgSpeed())
				.inputTime(resultSending.getInputTime())
				.scheduleTime(resultSending.getScheduleTime())
				.startTime(resultSending.getStartTime())
				.completeTime(resultSending.getCompleteTime())
				.logTime(resultSending.getLogTime())
				.build();
	}
}
