package com.rerelease.movie.rereleasemovie.alert.repository;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMovieAlertRepository extends JpaRepository<UserMovieAlert, Long> {
    Optional<UserMovieAlert> findByUserAndMovieId(Users user, Long movieId);

    List<UserMovieAlert> findAllByUserOrderByCreatedAtDesc(Users user);
}
