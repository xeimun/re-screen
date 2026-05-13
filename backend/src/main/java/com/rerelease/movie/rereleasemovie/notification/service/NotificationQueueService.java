package com.rerelease.movie.rereleasemovie.notification.service;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.alert.repository.UserMovieAlertRepository;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationQueue;
import com.rerelease.movie.rereleasemovie.notification.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationQueueService {

    private final NotificationQueueRepository notificationQueueRepository;
    private final UserMovieAlertRepository userMovieAlertRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final NotificationLogService notificationLogService;

    @Transactional
    public void addAlertToQueue(Long userMovieAlertId) {
        UserMovieAlert userMovieAlert = userMovieAlertRepository.findById(userMovieAlertId)
                                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                        "해당 알림이 존재하지 않습니다."));

        NotificationQueue notificationQueue = NotificationQueue.builder()
                                                               .userMovieAlert(userMovieAlert)
                                                               .retryCount(0)
                                                               .build();

        notificationQueueRepository.save(notificationQueue);
    }

    @Transactional
    public void sendNotification(NotificationQueue queue) {
        UserMovieAlert alert = queue.getUserMovieAlert();

        try {
            emailNotificationSender.send(alert);
            notificationLogService.saveSuccess(alert);
            notificationQueueRepository.delete(queue);
            userMovieAlertRepository.delete(alert);
        } catch (Exception e) {
            int updatedRetryCount = queue.getRetryCount() + 1;
            queue.updateRetryCount(updatedRetryCount);
            notificationQueueRepository.save(queue);

            if (updatedRetryCount >= 3) {
                notificationLogService.saveFailure(alert, e);
                notificationQueueRepository.delete(queue);
                userMovieAlertRepository.delete(alert);
            }
        }
    }

    @Transactional
    public void deleteByAlert(UserMovieAlert alert) {
        notificationQueueRepository.deleteByUserMovieAlert(alert);
    }
}
