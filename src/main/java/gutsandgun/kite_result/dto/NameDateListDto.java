package gutsandgun.kite_result.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NameDateListDto {


	private List<String> name;
	private List data;

	public NameDateListDto(List<String> name, List data) {
		this.name = name;
		this.data = data;
	}
}
