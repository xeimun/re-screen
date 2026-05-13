package com.rerelease.movie.rereleasemovie.auth.repository;

import com.rerelease.movie.rereleasemovie.auth.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
