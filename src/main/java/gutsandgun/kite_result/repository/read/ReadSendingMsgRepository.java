package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.SendingMsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadSendingMsgRepository extends JpaRepository<SendingMsg, Long> {
	Page<SendingMsg> findBySendingId(Long sendingId, Pageable pageable);
//	Page<SendingMsg> findByUserIdAndSendingId(String userId, Long sendingId, Pageable pageable);

}
