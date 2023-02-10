package gutsandgun.kite_result.dto;

import lombok.Getter;

@Getter
public class ResultTxSuccessDto {
	Long sendingId;
	Boolean success;

	Long count;

	public ResultTxSuccessDto() {
	}

	public ResultTxSuccessDto(Long sendingId, Boolean success, Long count) {
		this.sendingId = sendingId;
		this.success = success;
		this.count = count;
	}
}
