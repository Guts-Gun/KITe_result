package gutsandgun.kite_result.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ResultBrokerDto implements Serializable {
	private long id;

	private NameDateListDto brokerCount;
//	private Map<String, Map<Boolean, Long>> brokerSuccessFail;
	private NameDateListDto brokerSuccessFail;

	private NameDateListDto brokerSpeed;

	public ResultBrokerDto(long id, NameDateListDto brokerCount, NameDateListDto brokerSuccessFail, NameDateListDto brokerSpeed) {
		this.id = id;
		this.brokerCount = brokerCount;
		this.brokerSuccessFail = brokerSuccessFail;
		this.brokerSpeed = brokerSpeed;
	}
}

