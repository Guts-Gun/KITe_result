package gutsandgun.kite_result.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/result")
public class TestController {
	@GetMapping("/test")
	public String test() {
		return ("리턴되면 유효함");
	}
}
