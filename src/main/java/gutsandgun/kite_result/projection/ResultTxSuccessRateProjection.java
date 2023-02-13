package gutsandgun.kite_result.projection;

public interface ResultTxSuccessRateProjection {
	Long getSendingId();

	Boolean getSuccess();

	Long getCount();
}
