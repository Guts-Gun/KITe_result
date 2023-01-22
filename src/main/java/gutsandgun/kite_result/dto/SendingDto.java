package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.type.SendingRuleType;
import gutsandgun.kite_result.type.SendingType;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.Sending} entity
 */
@Data
public class SendingDto implements Serializable {
	private final Long id;
	private final String userId;
	private final SendingRuleType SendingRuleType;
	private final SendingType sendingType;
	private final Long totalMessage;
	private final Long inputTime;
	private final Long scheduleTime;
	private final String title;
	private final String media_link;
	private final String content;

	public SendingDto(Sending sending) {
		this.id = sending.getId();
		this.userId = sending.getUserId();
		this.SendingRuleType = sending.getSendingRuleType();
		this.sendingType = sending.getSendingType();
		this.totalMessage = sending.getTotalMessage();
		this.inputTime = sending.getInputTime();
		this.scheduleTime = sending.getScheduleTime();
		this.title = sending.getTitle();
		this.media_link = sending.getMedia_link();
		this.content = sending.getContent();
	}
}