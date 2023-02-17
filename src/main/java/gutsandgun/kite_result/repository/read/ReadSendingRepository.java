package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.Sending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadSendingRepository extends JpaRepository<Sending, Long> {
	List<Sending> findByUserIdAndIdIn(String userId, Collection<Long> ids);
	Optional<Sending> findByIdAndUserId(Long id, String userId);
	List<Sending> findByUserId(String userId);


}
