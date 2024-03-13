package hyeongseok.error.web.dto;

import hyeongseok.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResultDto {
    private HttpStatus httpStatus;
    private String message;
}
