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
	private final SendingRuleType sendingRuleType;
	private final SendingType sendingType;
	private final String replaceYn;
	private final Long totalMessage;
	private final Long inputTime;
	private final Long scheduleTime;
	private final String title;
	private final String mediaLink;
	private final String content;
	private final String sender;

	public SendingDto(Sending sending) {
		this.id = sending.getId();
		this.userId = sending.getUserId();
		this.sendingRuleType = sending.getSendingRuleType();
		this.sendingType = sending.getSendingType();
		this.totalMessage = sending.getTotalMessage();
		this.inputTime = sending.getInputTime();
		this.scheduleTime = sending.getScheduleTime();
		this.sender = sending.getSender();
		this.title = sending.getTitle();
		this.mediaLink = sending.getMediaLink();
		this.content = sending.getContent();
		this.replaceYn = sending.getReplaceYn();
	}
}