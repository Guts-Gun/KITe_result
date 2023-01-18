package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.LogSending;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadLogSendingRepository extends JpaRepository<LogSending, Long> {
	List<LogSending> findByUserIdOrderByInputTime(Long userId, Pageable pageable);
}
