package com.rerelease.movie.rereleasemovie.notification.scheduler;

import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.TmdbMovieListResponseDto;
import com.rerelease.movie.rereleasemovie.notification.entity.NotificationQueue;
import com.rerelease.movie.rereleasemovie.notification.repository.NotificationQueueRepository;
import com.rerelease.movie.rereleasemovie.notification.service.NotificationQueueService;
import com.rerelease.movie.rereleasemovie.movie.service.TmdbApiService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final TmdbApiService tmdbApiService;
    private final NotificationQueueService notificationQueueService;
    private final NotificationQueueRepository notificationQueueRepository;

    @Scheduled(cron = "0 0 4 * * ?")
    public void processNotifications() {
        Set<Long> availableMovieIds = new HashSet<>();

        // 모든 개봉 예정 영화 조회
        for (int page = 1; page <= tmdbApiService.getTotalPagesForUpcoming(); page++) {
            TmdbMovieListResponseDto upcomingMovies = tmdbApiService.getUpcomingMovies(page);
            if (upcomingMovies != null) {
                upcomingMovies.getResults()
                              .forEach(movie -> availableMovieIds.add(movie.getId()));
            }
        }

        // 모든 현재 개봉 중인 영화 조회
        for (int page = 1; page <= tmdbApiService.getTotalPagesForNowPlaying(); page++) {
            TmdbMovieListResponseDto nowPlayingMovies = tmdbApiService.getNowPlayingMovies(page);
            if (nowPlayingMovies != null) {
                nowPlayingMovies.getResults()
                                .forEach(movie -> availableMovieIds.add(movie.getId()));
            }
        }

        // 알림 큐에서 재시도 횟수 3 미만인 데이터 조회
        List<NotificationQueue> queues = notificationQueueRepository.findByRetryCountLessThan(3);

        // 알림 전송 실행
        for (NotificationQueue queue : queues) {
            if (availableMovieIds.contains(queue.getUserMovieAlert()
                                                .getMovieId())) {
                notificationQueueService.sendNotification(queue);
            }
        }
    }
}
