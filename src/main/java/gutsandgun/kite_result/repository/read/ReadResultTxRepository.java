package gutsandgun.kite_result.repository.read;

import gutsandgun.kite_result.entity.read.ResultTx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadResultTxRepository extends JpaRepository<ResultTx, Long> {
	Page<ResultTx> findByUserIdAndResultSendingId(String userId, Long resultSendingId, Pageable pageable);
	List<ResultTx> findByResultSendingId(Long resultSendingId);
}
