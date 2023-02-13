package gutsandgun.kite_result.projection;

public interface ResultTxTransferStatsProjection {
	Long getBrokerId();

	Double getAvgLatency();

	Long getCount();
}
