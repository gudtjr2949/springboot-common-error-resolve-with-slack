# Introduce

Spring Boot에서 발생하는 에러를 공통으로 처리하고, 에러가 발생했을 때, Slack 으로 알림을 보냄

</br>

# Install Slack Library

build.gradle 에 Slack 의존성을 설정함

```gradle

// Slack
implementation 'com.slack.api:slack-api-client:1.30.0'

```

</br>

# resources/application.yml

404 에러 공통 처리를 위해 throw-exception-if-no-handler-found 을 설정해줌
그리고 Slack에 사용할 WebHook URL 을 지정함 (이 URL은 Slack 에서 복사해야 함)

```yml
spring:
  # 404 에러용 설정
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false

webhook:
  slack:
    url: {SLACK_WEBHOOK_URL}
```

</br>

# Global Exception Handler

Spring 에서 발생하는 에러를 공통으로 처리하고, 클라이언트에게 응답할 형태를 커스텀함

```java
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final SlackService slackService;

    /**
    * 비즈니스 로직에서 발생할 수 있는 에러
    */
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        sendSlackMessage(e, errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(makeResponseBody(errorCode));
    }

    /**
     * Spring 에서 자주 발생하는 에러
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ErrorCode errorCode = makeErrorCode(ex);
        sendSlackMessage(ex, errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(makeResponseBody(errorCode));
    }

    /**
     * Spring 에서 자주 발생하는 에러를 CommonErrorCode 로 처리
     */
    private ErrorCode makeErrorCode(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return CommonErrorCode.BAD_REQUEST;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return CommonErrorCode.METHOD_NOT_ALLOWED;
        } else if (ex instanceof NoHandlerFoundException) {
            return CommonErrorCode.NOT_FOUND;
        } else if (ex instanceof HttpMessageNotReadableException) {
            return CommonErrorCode.BAD_REQUEST;
        }
        return CommonErrorCode.INTERNAL_SERVER_ERROR;
    }

    /**
     * 클라이언트에게 리턴할 JSON Body 형태 리턴
     */
    private ErrorResultDto makeResponseBody(ErrorCode errorCode) {
        return new ErrorResultDto(errorCode.getHttpStatus(), errorCode.getMessage());
    }


    /**
     * Error Message Send Slack
     */
    private void sendSlackMessage(Exception e, ErrorCode errorCode) {
        HashMap<String, String> message = new HashMap<>();
        message.put("에러 로그", e.getMessage());
        slackService.sendMessage(errorCode.getMessage(), message);
    }

}

```

</br>

# Blog

https://velog.io/@gudtjr2949/Spring-Boot-%EA%B3%B5%ED%86%B5-%EC%97%90%EB%9F%AC-%EA%B4%80%EB%A6%AC-Slack-%EC%95%8C%EB%A6%BC-1
