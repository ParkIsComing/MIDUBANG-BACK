package ewha.gsd.midubang.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException {
    private final String message;
    private final HttpStatus httpStatus;


    public ApiException(String message, HttpStatus badRequest) {
        this.message =message;
        this.httpStatus = badRequest;
    }
}