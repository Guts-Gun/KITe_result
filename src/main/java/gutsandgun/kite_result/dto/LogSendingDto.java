package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.LogSending;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.LogSending} entity
 */
@Data
public class LogSendingDto implements Serializable {
	private final Long id;
	private final Long userId;
	private final Long sendingId;
	private final Long logTime;
	private final String sendingType;
	private final String ruleType;
	private final String success;
	private final Long totalSending;
	private final Long failureSending;
	private final Float avgSpeed;
	private final Long requestTime;
	private final Long completeTime;
	private final Long scheduleTime;
	private final Long inputTime;


	public LogSendingDto(LogSending logSending) {
		this.id = logSending.getId();
		this.userId = logSending.getUserId();
		this.sendingId = logSending.getSendingId();
		this.logTime = logSending.getLogTime();
		this.sendingType = logSending.getSendingType();
		this.ruleType = logSending.getRuleType();
		this.success = logSending.getSuccess();
		this.totalSending = logSending.getTotalSending();
		this.failureSending = logSending.getFailureSending();
		this.avgSpeed = logSending.getAvgSpeed();
		this.inputTime = logSending.getInputTime();
		this.requestTime = logSending.getRequestTime();
		this.completeTime = logSending.getCompleteTime();
		this.scheduleTime = logSending.getScheduleTime();

	}
}