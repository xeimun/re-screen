package com.rerelease.movie.rereleasemovie.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.rerelease.movie.rereleasemovie.dto.ErrorResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@DisplayName("전역 예외 처리")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("로그인이 필요한 예외는 401 응답으로 변환한다")
    void handleAuthenticationRequiredReturnsUnauthorized() {
        ResponseEntity<ErrorResponseDto> response = handler.handleAuthenticationRequired(
                new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.")
        );

        assertError(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    @Test
    @DisplayName("중복 영화 등록 예외는 409 응답으로 변환한다")
    void handleMovieAlreadyRegisteredReturnsConflict() {
        ResponseEntity<ErrorResponseDto> response = handler.handleMovieAlreadyRegistered(
                new MovieAlreadyRegisteredException("이미 등록된 영화입니다.")
        );

        assertError(response, HttpStatus.CONFLICT, "이미 등록된 영화입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 알림 예외는 404 응답으로 변환한다")
    void handleAlertNotFoundReturnsNotFound() {
        ResponseEntity<ErrorResponseDto> response = handler.handleAlertNotFound(
                new AlertNotFoundException("해당 알림을 찾을 수 없습니다.")
        );

        assertError(response, HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("권한 없는 접근 예외는 403 응답으로 변환한다")
    void handleAccessDeniedReturnsForbidden() {
        ResponseEntity<ErrorResponseDto> response = handler.handleAccessDenied(
                new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.")
        );

        assertError(response, HttpStatus.FORBIDDEN, "본인의 알림만 삭제할 수 있습니다.");
    }

    private void assertError(ResponseEntity<ErrorResponseDto> response, HttpStatus status, String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ErrorResponseDto::getMessage)
                .isEqualTo(message);
    }
}
