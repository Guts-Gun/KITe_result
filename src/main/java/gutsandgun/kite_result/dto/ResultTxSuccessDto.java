package gutsandgun.kite_result.dto;

import lombok.Getter;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Getter
public class ResultTxSuccessDto {
	Long sendingId;
	Long successCnt = 0L;
	Long failCnt = 0L;

	public ResultTxSuccessDto() {
	}

	public ResultTxSuccessDto(Long sendingId, long success, long fail) {
		this.sendingId = sendingId;
		this.successCnt = success;
		this.failCnt = fail;
	}

	public ResultTxSuccessDto(Long sendingId, List<ResultTxSuccessRateProjection> resultTxSuccessRateProjections) {
		this.sendingId = sendingId;
		if (resultTxSuccessRateProjections == null){
			return;
		}

		for (ResultTxSuccessRateProjection resultTxSuccessRateProjection : resultTxSuccessRateProjections) {
			if (resultTxSuccessRateProjection.getSuccess() == TRUE) {
				this.successCnt = resultTxSuccessRateProjection.getCount();
			} else if (resultTxSuccessRateProjection.getSuccess() == FALSE) {
				this.failCnt = resultTxSuccessRateProjection.getCount();
			}
		}

	}

}
