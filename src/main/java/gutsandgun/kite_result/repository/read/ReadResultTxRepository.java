package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.entity.read.ResultTx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReadResultTxRepository extends JpaRepository<ResultTx, Long> {

	@Query(value =
			"SELECT rs.fk_sending_id as sendingId, rt.success, COUNT(rt.success) as count " +
					"from result_sending as rs, result_tx as rt " +
					"where rs.fk_user_id = :userId and rs.id = rt.fk_result_sending_id " +
					"group by rs.fk_sending_id, rt.success "
			, nativeQuery = true
	)
	List<ResultTxSuccessRateProjection> getTxSuccessCountGroupByResultSendingByUserId(@Param("userId") String userId);

	@Query(value =
			"SELECT rs.fk_sending_id as sendingId, rt.success, COUNT(rt.success) as count " +
					"from result_sending as rs, result_tx as rt " +
					"where rs.fk_user_id = :userId and rs.fk_sending_id In :sendingId and rs.id = rt.fk_result_sending_id " +
					"group by rs.fk_sending_id, rt.success "
			, nativeQuery = true
	)
	List<ResultTxSuccessRateProjection> getTxSuccessCountGroupByResultSendingByUserIdAndSendingId(@Param("userId") String userId, @Param("sendingId") List<Long> sendingId);


	List<ResultTx> findByUserIdAndResultSendingId(String userId, Long resultSendingId);

	List<ResultTx> findByUserIdAndTxIdIn(String userId, Collection<Long> txIds);


	ResultTx findByUserIdAndResultSendingIdAndTxId(String userId, Long resultSendingId, Long txId);




	List<ResultTx> findByResultSendingId(Long resultSendingId);
}
