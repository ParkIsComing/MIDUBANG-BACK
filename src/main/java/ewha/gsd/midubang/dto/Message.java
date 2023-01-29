package ewha.gsd.midubang.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Message {
    private HttpStatus status;
    private String message;

    public Message(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
