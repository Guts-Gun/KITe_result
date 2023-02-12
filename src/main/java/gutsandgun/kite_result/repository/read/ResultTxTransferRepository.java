package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.dto.ResultTxAvgLatencyProjection;
import gutsandgun.kite_result.entity.read.ResultTxTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultTxTransferRepository extends JpaRepository<ResultTxTransfer, Long> {

	@Query(value =


			"SELECT rs.fk_sending_id as sendingId, AVG(rtt.complete_time - rt.start_time) as avgLatency  " +
					"from result_sending as rs," +
					"     result_tx as rt, " +
					"     result_tx_transfer as rtt " +
					"where rs.fk_user_id = :userId " +
					"  and rs.fk_sending_id In :sendingId " +
					"  and rs.id = rt.fk_result_sending_id" +
					"  and rt.id = rtt.fk_result_tx_id " +
					"group by rs.fk_sending_id"
			, nativeQuery = true
	)
	List<ResultTxAvgLatencyProjection> getTxAvgLatencyGroupByResultSendingByUserIdAndSendingId(@Param("userId") String userId, @Param("sendingId") List<Long> sendingId);
}