package gutsandgun.kite_result.dto;

import lombok.Data;

import java.util.List;

@Data
public class NameDateListDto {


	private List<String> name;
	private List<String> color;
	private List data;


	public NameDateListDto(List<String> name, List<String> color, List data) {
		this.name = name;
		this.color = color;
		this.data = data;
	}
}
