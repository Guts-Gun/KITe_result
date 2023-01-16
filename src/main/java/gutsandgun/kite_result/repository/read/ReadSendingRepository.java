package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.Sending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadSendingRepository extends JpaRepository<Sending, Long> {
	List<Sending> findByUserId(Long userId);


}
