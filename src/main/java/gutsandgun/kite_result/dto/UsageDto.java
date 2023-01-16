package gutsandgun.kite_result.dto;

import lombok.Data;

@Data
public class UsageDto {
	private final String type;
	private final Long usageCap;
	private final Long usage;

}
