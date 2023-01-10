package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.SendingBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadSendingBlockRepository extends JpaRepository<SendingBlock, Long> {
}
