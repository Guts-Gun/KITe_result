package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.SendingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadSendingScheduleRepository extends JpaRepository<SendingSchedule, Long> {
}
