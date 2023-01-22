package gutsandgun.kite_result.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "S000", "잘못된 요청입니다.");


    private int status;
    private String code;
    private String message;

}
