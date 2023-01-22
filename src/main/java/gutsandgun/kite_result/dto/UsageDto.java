package gutsandgun.kite_result.dto;

import gutsandgun.kite_result.type.SendingType;
import lombok.Data;

@Data
public class UsageDto {
	private final SendingType type;
	private final Long usageCap;
	private final Long usage;

}
