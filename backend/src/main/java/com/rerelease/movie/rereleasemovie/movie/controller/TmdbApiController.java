package com.rerelease.movie.rereleasemovie.movie.controller;

import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.TmdbMovieListResponseDto;
import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.search.TmdbMovieSearchResponseDto;
import com.rerelease.movie.rereleasemovie.movie.service.TmdbApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
public class TmdbApiController {

    private final TmdbApiService tmdbApiService;

    @GetMapping("/upcoming")
    public ResponseEntity<TmdbMovieListResponseDto> getUpcomingMovies(@RequestParam(defaultValue = "1") int page) {
        TmdbMovieListResponseDto movies = tmdbApiService.getUpcomingMovies(page);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/upcoming/total-pages")
    public ResponseEntity<Integer> getTotalPages() {
        int totalPages = tmdbApiService.getTotalPagesForUpcoming();
        return ResponseEntity.ok(totalPages);
    }

    @GetMapping("/search")
    public ResponseEntity<TmdbMovieSearchResponseDto> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page
    ) {
        return ResponseEntity.ok(tmdbApiService.searchMovies(query, page));
    }
}
