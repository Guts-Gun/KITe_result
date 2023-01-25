package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.ResultSending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadResultSendingRepository extends JpaRepository<ResultSending, Long> {
	ResultSending findBySendingId(Long sendingId);
	Page<ResultSending> findByUserId(String userId, Pageable pageable);


	ResultSending findByUserIdAndSendingId(String user, Long sendingId);
}
