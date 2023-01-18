package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.Sending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadSendingRepository extends JpaRepository<Sending, Long> {
	List<Sending> findByUserId(Long userId);


}
