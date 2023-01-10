package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadUserPhoneRepository extends JpaRepository<UserPhone, Long> {
}
