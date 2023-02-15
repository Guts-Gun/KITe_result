package gutsandgun.kite_result.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	BAD_REQUEST(400, "S000", "잘못된 요청입니다."),


	USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
	SENDING_NOT_FOUND(404, "S001", "발송을 찾을 수 없습니다."),
	TX_NOT_FOUND(404, "T001", "TX를 찾을 수 없습니다."),

	RESULT_SENDING_NOT_FOUND(404, "R001", "발송결과를 찾을 수 없습니다."),
	RESULT_TX_NOT_FOUND(404, "S002", "발송을 찾을 수 없습니다."),
	MOL_RU_NOT_FOUND(404, "S999", "암튼 틀림.");
	private int status;
	private String code;
	private String message;

}
