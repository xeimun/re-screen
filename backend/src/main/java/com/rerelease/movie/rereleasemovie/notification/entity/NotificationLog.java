package com.rerelease.movie.rereleasemovie.notification.entity;

import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private Users user;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "poster_path", length = 500)
    private String posterPath;

    @Column(name = "notification_type", nullable = false, length = 20)
    private String notificationType;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public NotificationLog(Users user, Long movieId, String movieTitle, String posterPath, String notificationType,
                           int status, String errorMessage, LocalDateTime registeredAt) {
        this.user = user;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterPath = posterPath;
        this.notificationType = notificationType;
        this.status = status;
        this.errorMessage = errorMessage;
        this.registeredAt = registeredAt;
    }
}
