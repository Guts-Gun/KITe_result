package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadBrokerRepository extends JpaRepository<Broker, Long> {
}
