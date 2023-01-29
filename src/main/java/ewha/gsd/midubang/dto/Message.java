package ewha.gsd.midubang.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Message {
    private HttpStatus status;
    private String message;
}
