package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.ResultSending;
import gutsandgun.kite_result.entity.read.Sending;
import gutsandgun.kite_result.type.SendingRuleType;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.Sending} entity
 */
@Data
public class SendingShortInfoDto implements Serializable {
	private final Long id;
	private final SendingStatus sendingStatus;
	private Long inputTime;
	private final SendingRuleType sendingRuleType;
	private final SendingType sendingType;
	private final Long totalMessage;
	private final String title;
	private final String mediaLink;
	private final String content;
	private final String sender;

	private final ResultTxSuccessDto resultTxSuccessDto;

	public SendingShortInfoDto(Sending sending, ResultSending resultSending, ResultTxSuccessDto resultTxSuccessDto) {
		this.id = sending.getId();

		if (resultTxSuccessDto != null) {
			this.sendingStatus = resultSending.getSendingStatus();
		} else
			this.sendingStatus = SendingStatus.PENDING;


		this.inputTime = sending.getInputTime();
		this.sendingRuleType = sending.getSendingRuleType();
		this.sendingType = sending.getSendingType();
		this.totalMessage = sending.getTotalMessage();
		this.title = sending.getTitle();
		this.mediaLink = sending.getMediaLink();
		this.content = sending.getContent();
		this.sender = sending.getSender();

		this.resultTxSuccessDto = Objects.requireNonNullElseGet(resultTxSuccessDto, () -> new ResultTxSuccessDto(sending.getId(), 0, 0));

	}

}