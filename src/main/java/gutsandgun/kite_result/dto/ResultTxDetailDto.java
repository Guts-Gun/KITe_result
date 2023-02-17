package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.entity.read.ResultTx;
import gutsandgun.kite_result.entity.read.SendingMsg;
import gutsandgun.kite_result.type.FailReason;
import gutsandgun.kite_result.type.SendingType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link ResultTx} entity
 */
@Data
@Builder
public class ResultTxDetailDto implements Serializable {
	private final Long id;
	private final String userId;
	private final Long resultSendingId;
	private final Long txId;
	private final Long brokerId;
	private final SendingType sendingType;
	private final String sender;
	private final String receiver;
	private final Boolean success;
	private final FailReason failReason;
	private final String title;
	private final String mediaLink;
	private final String content;
	private final Long inputTime;
	private final Long scheduleTime;
	private final Long startTime;
	private final Long sendTime;
	private final Long completeTime;

	private final List<ResultTxTransferDto> resultTxTransferList;

	public static ResultTxDetailDto toDto(SendingMsg sendingMsg,  ResultTx resultTx, List<ResultTxTransferDto> resultTxTransferList) {
		return ResultTxDetailDto.builder()
				.id(sendingMsg.getId())
				.userId(resultTx.getUserId())
				.resultSendingId(resultTx.getResultSendingId())
				.txId(sendingMsg.getId())
				.brokerId(resultTx.getBrokerId())
				.sendingType(resultTx.getSendingType())
				.sender(sendingMsg.getSender())
				.receiver(sendingMsg.getReceiver())
				.success(resultTx.getSuccess())
				.failReason(resultTx.getFailReason())
				.title(resultTx.getTitle())
				.mediaLink(resultTx.getMediaLink())
				.content(resultTx.getContent())
				.inputTime(resultTx.getInputTime())
//				.completeTime(resultTx.getCompleteTime())
				.resultTxTransferList(resultTxTransferList)
				.build();
	}
}