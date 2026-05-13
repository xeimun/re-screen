package com.rerelease.movie.rereleasemovie.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertRequest;
import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertResponse;
import com.rerelease.movie.rereleasemovie.common.exception.AlertNotFoundException;
import com.rerelease.movie.rereleasemovie.common.exception.MovieAlreadyRegisteredException;
import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import com.rerelease.movie.rereleasemovie.alert.repository.UserMovieAlertRepository;
import com.rerelease.movie.rereleasemovie.auth.repository.UserRepository;
import com.rerelease.movie.rereleasemovie.notification.service.NotificationQueueService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("영화 알림 서비스")
class MovieAlertServiceTest {

    @Mock
    private UserMovieAlertRepository userMovieAlertRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationQueueService notificationQueueService;

    private MovieAlertService movieAlertService;

    @BeforeEach
    void setUp() {
        movieAlertService = new MovieAlertService(
                userMovieAlertRepository,
                userRepository,
                notificationQueueService
        );
    }

    @Test
    @DisplayName("알림 등록 시 UserMovieAlert를 저장하고 NotificationQueue에 추가한다")
    void registerMovieAlertSavesAlertAndAddsNotificationQueue() {
        Users user = createUser(1L, "user@example.com");
        MovieAlertRequest request = createRequest(100L, "Inception", "/poster.jpg");
        UserMovieAlert savedAlert = createAlert(10L, user, 100L, "Inception", "/poster.jpg");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMovieAlertRepository.findByUserAndMovieId(user, 100L)).thenReturn(Optional.empty());
        when(userMovieAlertRepository.save(any(UserMovieAlert.class))).thenReturn(savedAlert);

        MovieAlertResponse response = movieAlertService.registerMovieAlert("user@example.com", request);

        assertThat(response.getUserMovieAlertId()).isEqualTo(10L);
        assertThat(response.getMovieId()).isEqualTo(100L);
        assertThat(response.getMovieTitle()).isEqualTo("Inception");
        verify(notificationQueueService).addAlertToQueue(10L);
    }

    @Test
    @DisplayName("같은 사용자가 같은 영화를 중복 등록하면 예외가 발생한다")
    void registerMovieAlertRejectsDuplicateMovieForSameUser() {
        Users user = createUser(1L, "user@example.com");
        MovieAlertRequest request = createRequest(100L, "Inception", "/poster.jpg");
        UserMovieAlert existingAlert = createAlert(10L, user, 100L, "Inception", "/poster.jpg");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMovieAlertRepository.findByUserAndMovieId(user, 100L)).thenReturn(Optional.of(existingAlert));

        assertThatThrownBy(() -> movieAlertService.registerMovieAlert("user@example.com", request))
                .isInstanceOf(MovieAlreadyRegisteredException.class)
                .hasMessage("이미 등록된 영화입니다.");

        verify(userMovieAlertRepository, never()).save(any(UserMovieAlert.class));
        verify(notificationQueueService, never()).addAlertToQueue(any());
    }

    @Test
    @DisplayName("본인의 알림 삭제 시 NotificationQueue를 먼저 삭제하고 UserMovieAlert를 삭제한다")
    void deleteUserMovieAlertDeletesNotificationQueueBeforeAlert() {
        Users user = createUser(1L, "user@example.com");
        UserMovieAlert alert = createAlert(10L, user, 100L, "Inception", "/poster.jpg");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMovieAlertRepository.findById(10L)).thenReturn(Optional.of(alert));

        movieAlertService.deleteUserMovieAlert("user@example.com", 10L);

        verify(notificationQueueService).deleteByAlert(alert);
        verify(userMovieAlertRepository).delete(alert);
    }

    @Test
    @DisplayName("존재하지 않는 알림을 삭제하려 하면 알림 없음 예외가 발생한다")
    void deleteUserMovieAlertRejectsMissingAlert() {
        Users user = createUser(1L, "user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMovieAlertRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieAlertService.deleteUserMovieAlert("user@example.com", 10L))
                .isInstanceOf(AlertNotFoundException.class)
                .hasMessage("해당 알림을 찾을 수 없습니다.");

        verify(userMovieAlertRepository, never()).delete(any(UserMovieAlert.class));
    }

    @Test
    @DisplayName("다른 사용자의 알림을 삭제하려 하면 접근 거부 예외가 발생한다")
    void deleteUserMovieAlertRejectsOtherUsersAlert() {
        Users owner = createUser(1L, "owner@example.com");
        Users otherUser = createUser(2L, "other@example.com");
        UserMovieAlert alert = createAlert(10L, otherUser, 100L, "Inception", "/poster.jpg");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(userMovieAlertRepository.findById(10L)).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> movieAlertService.deleteUserMovieAlert("owner@example.com", 10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("본인의 알림만 삭제할 수 있습니다.");

        verify(userMovieAlertRepository, never()).delete(any(UserMovieAlert.class));
    }

    private Users createUser(Long id, String email) {
        Users user = Users.builder()
                          .email(email)
                          .password("encoded-password")
                          .nickname("nickname")
                          .emailVerified(false)
                          .role(Users.Role.ROLE_USER)
                          .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private MovieAlertRequest createRequest(Long tmdbId, String title, String posterPath) {
        MovieAlertRequest request = new MovieAlertRequest();
        ReflectionTestUtils.setField(request, "tmdbId", tmdbId);
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "posterPath", posterPath);
        return request;
    }

    private UserMovieAlert createAlert(Long id, Users user, Long movieId, String movieTitle, String posterPath) {
        UserMovieAlert alert = UserMovieAlert.builder()
                                             .user(user)
                                             .movieId(movieId)
                                             .movieTitle(movieTitle)
                                             .posterPath(posterPath)
                                             .build();
        ReflectionTestUtils.setField(alert, "id", id);
        return alert;
    }
}
