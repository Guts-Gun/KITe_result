package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface ReadBrokerRepository extends JpaRepository<Broker, Long> {



	default Map<Long, Broker> findAllMap() {
		return findAll().stream().collect(Collectors.toMap(Broker::getId, Broker -> Broker ));
	}
}
