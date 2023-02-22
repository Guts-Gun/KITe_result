package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.projection.ResultTxSuccessRateProjection;
import gutsandgun.kite_result.projection.TotalUsage;
import gutsandgun.kite_result.entity.read.ResultSending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadResultSendingRepository extends JpaRepository<ResultSending, Long> {
	Optional<ResultSending> findBySendingId(Long sendingId);

	List<ResultSending> findBySendingIdIn(Collection<Long> sendingIds);






	@Query("select r from ResultSending r where r.userId = ?1")
	List<ResultSending> findAllByUserId(String userId);


	Page<ResultSending> findByUserId(String userId, Pageable pageable);


	Optional<ResultSending> findByUserIdAndSendingId(String user, Long sendingId);

	@Query(value =
			"SELECT " +
					"rs.sending_type AS sendingType, " +
					"SUM(rs.total_message) AS totalUsage " +
					"FROM result_sending AS rs " +
					"WHERE rs.fk_user_id = :userId " +
					"GROUP BY rs.sending_type "
			, nativeQuery = true
	)
	List<TotalUsage> findTotalUsageBySendingTypeAndUserId(
			@Param("userId") String userId);

	@Query(value =
			"SELECT rs.fk_sending_id as sendingId, rt.status, COUNT(rt.status) as count " +
					"from result_sending as rs, result_tx as rt " +
					"where rs.fk_sending_id In :sendingId and rs.id = rt.fk_result_sending_id " +
					"group by rs.fk_sending_id, rt.status "
			, nativeQuery = true
	)
	List<ResultTxSuccessRateProjection> getTxSuccessCountGroupByResultSendingByUserIdAndSendingId(@Param("sendingId") List<Long> sendingId);

}
