package com.rerelease.movie.rereleasemovie.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationLog;
import com.rerelease.movie.rereleasemovie.notification.repository.NotificationLogRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 로그 서비스")
class NotificationLogServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    private NotificationLogService notificationLogService;

    @BeforeEach
    void setUp() {
        notificationLogService = new NotificationLogService(notificationLogRepository);
    }

    @Test
    @DisplayName("성공 로그는 EMAIL 타입과 성공 상태로 저장한다")
    void saveSuccessStoresEmailSuccessLog() {
        UserMovieAlert alert = createAlert();

        notificationLogService.saveSuccess(alert);

        NotificationLog savedLog = captureSavedLog();
        assertThat(savedLog.getNotificationType()).isEqualTo("EMAIL");
        assertThat(savedLog.getStatus()).isEqualTo(1);
        assertThat(savedLog.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("실패 로그는 EMAIL 타입과 실패 상태, 예외명을 저장한다")
    void saveFailureStoresEmailFailureLog() {
        UserMovieAlert alert = createAlert();

        notificationLogService.saveFailure(alert, new IllegalStateException("failed"));

        NotificationLog savedLog = captureSavedLog();
        assertThat(savedLog.getNotificationType()).isEqualTo("EMAIL");
        assertThat(savedLog.getStatus()).isEqualTo(2);
        assertThat(savedLog.getErrorMessage()).isEqualTo("IllegalStateException");
    }

    private NotificationLog captureSavedLog() {
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(captor.capture());
        return captor.getValue();
    }

    private UserMovieAlert createAlert() {
        Users user = Users.builder()
                          .email("user@example.com")
                          .password("encoded-password")
                          .nickname("nickname")
                          .emailVerified(false)
                          .role(Users.Role.ROLE_USER)
                          .build();

        UserMovieAlert alert = UserMovieAlert.builder()
                                             .user(user)
                                             .movieId(100L)
                                             .movieTitle("Inception")
                                             .posterPath("/poster.jpg")
                                             .build();
        ReflectionTestUtils.setField(alert, "createdAt", LocalDateTime.of(2026, 5, 14, 6, 0));
        return alert;
    }
}
