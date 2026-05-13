package com.rerelease.movie.rereleasemovie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReReleaseMovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReReleaseMovieApplication.class, args);
    }

}
