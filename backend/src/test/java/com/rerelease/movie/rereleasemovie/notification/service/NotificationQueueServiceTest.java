package com.rerelease.movie.rereleasemovie.notification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.alert.repository.UserMovieAlertRepository;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationQueue;
import com.rerelease.movie.rereleasemovie.notification.repository.NotificationQueueRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 큐 서비스")
class NotificationQueueServiceTest {

    @Mock
    private NotificationQueueRepository notificationQueueRepository;

    @Mock
    private UserMovieAlertRepository userMovieAlertRepository;

    @Mock
    private EmailNotificationSender emailNotificationSender;

    @Mock
    private NotificationLogService notificationLogService;

    private NotificationQueueService notificationQueueService;

    @BeforeEach
    void setUp() {
        notificationQueueService = new NotificationQueueService(
                notificationQueueRepository,
                userMovieAlertRepository,
                emailNotificationSender,
                notificationLogService
        );
    }

    @Test
    @DisplayName("알림 등록 시 UserMovieAlert를 찾아 NotificationQueue에 추가한다")
    void addAlertToQueueSavesNotificationQueue() {
        UserMovieAlert alert = createAlert();

        when(userMovieAlertRepository.findById(10L)).thenReturn(Optional.of(alert));

        notificationQueueService.addAlertToQueue(10L);

        verify(notificationQueueRepository).save(any(NotificationQueue.class));
    }

    @Test
    @DisplayName("존재하지 않는 알림을 큐에 추가하려 하면 예외가 발생한다")
    void addAlertToQueueRejectsMissingAlert() {
        when(userMovieAlertRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationQueueService.addAlertToQueue(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 알림이 존재하지 않습니다.");

        verify(notificationQueueRepository, never()).save(any(NotificationQueue.class));
    }

    @Test
    @DisplayName("메일 발송 성공 시 성공 로그를 저장하고 사용자 알림을 삭제한다")
    void sendNotificationSavesSuccessLogAndDeletesAlert() throws Exception {
        UserMovieAlert alert = createAlert();
        NotificationQueue queue = createQueue(alert, 0);

        notificationQueueService.sendNotification(queue);

        verify(emailNotificationSender).send(alert);
        verify(notificationLogService).saveSuccess(alert);
        InOrder inOrder = inOrder(notificationQueueRepository, userMovieAlertRepository);
        inOrder.verify(notificationQueueRepository).delete(queue);
        inOrder.verify(userMovieAlertRepository).delete(alert);
        verify(notificationQueueRepository, never()).save(any(NotificationQueue.class));
    }

    @Test
    @DisplayName("메일 발송 실패가 3회 미만이면 재시도 횟수만 증가시킨다")
    void sendNotificationIncrementsRetryCountWhenFailureIsRetryable() throws Exception {
        UserMovieAlert alert = createAlert();
        NotificationQueue queue = createQueue(alert, 1);
        RuntimeException failure = new RuntimeException("mail failed");

        org.mockito.Mockito.doThrow(failure)
                           .when(emailNotificationSender)
                           .send(alert);

        notificationQueueService.sendNotification(queue);

        verify(notificationQueueRepository).save(queue);
        verify(notificationQueueRepository, never()).delete(any(NotificationQueue.class));
        verify(notificationLogService, never()).saveFailure(any(UserMovieAlert.class), any(Exception.class));
        verify(userMovieAlertRepository, never()).delete(any(UserMovieAlert.class));
    }

    @Test
    @DisplayName("메일 발송 실패가 3회에 도달하면 실패 로그를 저장하고 사용자 알림을 삭제한다")
    void sendNotificationSavesFailureLogAndDeletesAlertWhenRetryLimitReached() throws Exception {
        UserMovieAlert alert = createAlert();
        NotificationQueue queue = createQueue(alert, 2);
        RuntimeException failure = new RuntimeException("mail failed");

        org.mockito.Mockito.doThrow(failure)
                           .when(emailNotificationSender)
                           .send(alert);

        notificationQueueService.sendNotification(queue);

        verify(notificationQueueRepository).save(queue);
        verify(notificationLogService).saveFailure(alert, failure);
        InOrder inOrder = inOrder(notificationQueueRepository, userMovieAlertRepository);
        inOrder.verify(notificationQueueRepository).delete(queue);
        inOrder.verify(userMovieAlertRepository).delete(alert);
    }

    @Test
    @DisplayName("사용자 알림 삭제 시 연결된 NotificationQueue를 삭제한다")
    void deleteByAlertDeletesQueueByUserMovieAlert() {
        UserMovieAlert alert = createAlert();

        notificationQueueService.deleteByAlert(alert);

        verify(notificationQueueRepository).deleteByUserMovieAlert(alert);
    }

    private NotificationQueue createQueue(UserMovieAlert alert, int retryCount) {
        return NotificationQueue.builder()
                                .userMovieAlert(alert)
                                .retryCount(retryCount)
                                .build();
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
        ReflectionTestUtils.setField(alert, "id", 10L);
        return alert;
    }
}
