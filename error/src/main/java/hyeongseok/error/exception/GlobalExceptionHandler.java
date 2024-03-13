package hyeongseok.error.exception;

import hyeongseok.error.web.dto.ErrorResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResultDto(errorCode.getHttpStatus(), errorCode.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResultDto(HttpStatus.BAD_REQUEST, "잘못된 파라미터 타입입니다."));
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResultDto(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP 요청입니다."));
        } else if (ex instanceof NoHandlerFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResultDto(HttpStatus.NOT_FOUND, "요청한 메서드를 찾을 수 없습니다."));
        } else if (ex instanceof HttpMessageNotReadableException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResultDto(HttpStatus.BAD_REQUEST, "잘못된 DTO 객체 바인딩 요청입니다."));
        }

        return null;
    }
}
