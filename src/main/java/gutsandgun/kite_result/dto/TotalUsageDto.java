package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.type.SendingType;
import lombok.Data;

@Data
public class TotalUsageDto {
	private final SendingType sendingType;
	private final Long totalUsage;
	private final Long usageCap;

	public TotalUsageDto(SendingType sendingType, Long totalUsage, Long usageCap) {
		this.sendingType = sendingType;
		this.totalUsage = totalUsage;
		this.usageCap = usageCap;
	}

	public TotalUsageDto(Integer sendingTypeId, Long totalUsage, Long usageCap) {
		this.sendingType = SendingType.values()[sendingTypeId];
		this.totalUsage = totalUsage;
		this.usageCap = usageCap;
	}
}
