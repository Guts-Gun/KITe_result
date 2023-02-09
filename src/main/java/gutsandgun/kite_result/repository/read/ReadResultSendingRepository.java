package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.dto.TotalUsage;
import gutsandgun.kite_result.dto.TotalUsageDto;
import gutsandgun.kite_result.entity.read.ResultSending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadResultSendingRepository extends JpaRepository<ResultSending, Long> {
	ResultSending findBySendingId(Long sendingId);

	Page<ResultSending> findByUserId(String userId, Pageable pageable);


	ResultSending findByUserIdAndSendingId(String user, Long sendingId);

	@Query(value =
			"SELECT " +
					" rs.sending_type AS sendingType, " +
					"SUM(rs.total_message) AS totalUsage " +
					"FROM result_sending AS rs " +
					"WHERE rs.fk_user_id = :userId " +
					"GROUP BY rs.sending_type "
			, nativeQuery = true
	)
	List<TotalUsage> findTotalUsageBySendingTypeAndUserId(
			@Param("userId") String userId);
}
