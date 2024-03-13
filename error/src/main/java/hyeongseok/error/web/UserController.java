package hyeongseok.error.web;

import hyeongseok.error.exception.CommonErrorCode;
import hyeongseok.error.exception.RestApiException;
import hyeongseok.error.web.dto.UserResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/un-authorized")
    public ResponseEntity<UserResultDto> unAuthorized() {
        throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
    }

    @GetMapping("/expired-refresh-token")
    public ResponseEntity<UserResultDto> expiredRefreshToken() {
        throw new RestApiException(CommonErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    // 잘못된 파라미터 요청
}