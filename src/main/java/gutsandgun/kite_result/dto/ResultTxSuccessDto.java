package gutsandgun.kite_result.dto;

import com.querydsl.core.annotations.QueryProjection;
import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.type.SendingStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ResultTxSuccessDto {
	Long sendingId;
	Long successCnt = 0L;
	Long failCnt = 0L;

	public ResultTxSuccessDto() {
	}

	@QueryProjection
	public ResultTxSuccessDto(Long sendingId, long success, long fail) {
		this.sendingId = sendingId;
		this.successCnt = success;
		this.failCnt = fail;
	}

	public ResultTxSuccessDto(Long sendingId, List<ResultTxSuccessRateProjection> resultTxSuccessRateProjections) {
		this.sendingId = sendingId;
		if (resultTxSuccessRateProjections == null) {
			return;
		}

		for (ResultTxSuccessRateProjection resultTxSuccessRateProjection : resultTxSuccessRateProjections) {
			if (SendingStatus.values()[resultTxSuccessRateProjection.getStatus()] == SendingStatus.COMPLETE) {
				this.successCnt = resultTxSuccessRateProjection.getCount();
			} else if (SendingStatus.values()[resultTxSuccessRateProjection.getStatus()] == SendingStatus.FAIL) {
				this.failCnt = resultTxSuccessRateProjection.getCount();
			}
		}

	}

}
