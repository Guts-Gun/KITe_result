package gutsandgun.kite_result.dto;

public interface ResultTxSuccessRateProjection {
	Long getSendingId();

	Boolean getSuccess();

	Long getCount();
}
