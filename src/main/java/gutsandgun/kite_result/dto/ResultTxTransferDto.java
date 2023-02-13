package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.type.FailReason;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link gutsandgun.kite_result.entity.read.ResultTxTransfer} entity
 */
@Data
public class ResultTxTransferDto implements Serializable {
	private final Long id;
	private final Long txId;
	private final Long brokerId;
	private final Boolean success;
	private final FailReason failReason;
	private final Long sendTime;
	private final Long completeTime;
}