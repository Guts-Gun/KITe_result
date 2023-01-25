package gutsandgun.kite_result.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ResultBrokerDto {
	private long id;

	private NameDateListDto brokerCount;
	private Map<String, Map<Boolean, Long>> brokerSuccessFail;
	private NameDateListDto brokerSpeed;

	public ResultBrokerDto(long id, Map<String, Long> brokerCount, Map<String, Map<Boolean, Long>> brokerSuccessFail, Map<String, LongSummaryStatistics> brokerSpeed) {
		this.id = id;
		this.brokerCount = new NameDateListDto( brokerCount.keySet().stream().toList(), new ArrayList<>(brokerCount.values()));
		this.brokerSuccessFail = brokerSuccessFail;
		this.brokerSpeed = new NameDateListDto( brokerSpeed.keySet().stream().toList(), new ArrayList<>(brokerSpeed.values().stream().map(longSummaryStatistics -> longSummaryStatistics.getAverage()).collect(Collectors.toList())));;
	}
}

