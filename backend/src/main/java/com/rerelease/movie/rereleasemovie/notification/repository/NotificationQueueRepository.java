package com.rerelease.movie.rereleasemovie.notification.repository;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationQueue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, Long> {

    List<NotificationQueue> findByRetryCountLessThan(int retryCount);

    void deleteByUserMovieAlert(UserMovieAlert userMovieAlert);
}
