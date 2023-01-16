package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.Sending;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.Sending} entity
 */
@Data
public class SendingDto implements Serializable {
	private final Long id;
	private final Long userId;
	private final String ruleType;
	private final String sendingType;
	private final Long totalSending;
	private final Long requestTime;
	private final Long scheduleTime;
	private final String title;
	private final String media_link;
	private final String content;

	public SendingDto(Sending sending) {
		this.id = sending.getId();
		this.userId = sending.getUserId();
		this.ruleType = sending.getRuleType();
		this.sendingType = sending.getSendingType();
		this.totalSending = sending.getTotalSending();
		this.requestTime = sending.getRequestTime();
		this.scheduleTime = sending.getScheduleTime();
		this.title = sending.getTitle();
		this.media_link = sending.getMedia_link();
		this.content = sending.getContent();
	}

}