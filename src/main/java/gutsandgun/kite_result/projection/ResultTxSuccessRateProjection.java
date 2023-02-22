package gutsandgun.kite_result.projection;

public interface ResultTxSuccessRateProjection {
	Long getSendingId();

	int getStatus();

	Long getCount();
}
