package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.LogSending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadLogSendingRepository extends JpaRepository<LogSending, Long> {
	LogSending findBySendingId(Long sendingId);

	LogSending findByUserIdAndSendingId(Long userId, Long sendingId);
	List<LogSending> findByUserId(Long userId);

}
