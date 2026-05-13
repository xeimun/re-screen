package com.rerelease.movie.rereleasemovie.notification.repository;

import com.rerelease.movie.rereleasemovie.notification.entity.NotificationQueue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, Long> {

    // 재시도 횟수가 3회 미만인 알림을 조회
    List<NotificationQueue> findByRetryCountLessThan(int retryCount);
}
