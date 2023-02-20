package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.dto.ResultTxTransferDto;
import gutsandgun.kite_result.entity.read.ResultTxTransfer;
import gutsandgun.kite_result.projection.ResultTxAvgLatencyProjection;
import gutsandgun.kite_result.projection.ResultTxTransferStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResultTxTransferRepository extends JpaRepository<ResultTxTransfer, Long> {
	Optional<ResultTxTransfer> findFirstByResultTxIdInOrderByCompleteTimeDesc(Collection<Long> resultTxIds);
//	ResultTxTransfer findFirstByResultTxIdOrderByCompleteTimeDesc(Long resultTxId);



	List<ResultTxTransfer> findByResultTxIdIn(List<Long> resultTxIdList);


	@Query(value =
			"SELECT rs.fk_sending_id as sendingId, AVG(rtt.complete_time - rt.start_time) as avgLatency  " +
					"from result_sending as rs," +
					"     result_tx as rt, " +
					"     result_tx_transfer as rtt " +
					"where rs.fk_sending_id In :sendingId " +
					"  and rs.id = rt.fk_result_sending_id" +
					"  and rt.id = rtt.fk_result_tx_id " +
					"group by rs.fk_sending_id"
			, nativeQuery = true
	)
	List<ResultTxAvgLatencyProjection> getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(@Param("sendingId") List<Long> sendingId);


	@Query(value =
			"select fk_broker_id as brokerId, " +
					"AVG(rtt.complete_time - rtt.send_time) as avgLatency, " +
					"COUNT(*) as count\n" +
					"from result_tx_transfer as rtt " +
					"where rtt.fk_result_tx_id In :sendingId " +
					"group by fk_broker_id"
			, nativeQuery = true)
	List<ResultTxTransferStatsProjection> getTxTransferAvgLatencyGroupByBrokerId(@Param("sendingId") List<Long> sendingId);

	List<ResultTxTransferDto> findByResultTxId(Long txId);





}