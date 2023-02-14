package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.type.FailReason;
import gutsandgun.kite_result.type.SendingType;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.ResultTxTransfer} entity
 */
@Data
public class ResultTxTransferDto implements Serializable {
	private final Long id;
	private final Long resultTxId;
	private final Long brokerId;
	private final SendingType sendingType;
	private final String sender;
	private final String receiver;
	private final Boolean success;
	private final FailReason failReason;
	private final Long sendTime;
	private final Long completeTime;


}