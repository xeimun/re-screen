package com.rerelease.movie.rereleasemovie.movie.service;

import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.TmdbMovieListResponseDto;
import com.rerelease.movie.rereleasemovie.movie.dto.tmdb.search.TmdbMovieSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TmdbApiService {

    private static final String LANGUAGE = "ko-KR";
    private static final String REGION = "KR";
    private static final int DEFAULT_TOTAL_PAGES = 1;

    private final WebClient tmdbWebClient;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    public TmdbMovieListResponseDto getUpcomingMovies(int page) {
        return tmdbWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/movie/upcoming")
                                    .queryParam("api_key", tmdbApiKey)
                                    .queryParam("language", LANGUAGE)
                                    .queryParam("region", REGION)
                                    .queryParam("page", page)
                                    .build())
                            .retrieve()
                            .bodyToMono(TmdbMovieListResponseDto.class)
                            .block();
    }

    public TmdbMovieListResponseDto getNowPlayingMovies(int page) {
        return tmdbWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/movie/now_playing")
                                    .queryParam("api_key", tmdbApiKey)
                                    .queryParam("language", LANGUAGE)
                                    .queryParam("region", REGION)
                                    .queryParam("page", page)
                                    .build())
                            .retrieve()
                            .bodyToMono(TmdbMovieListResponseDto.class)
                            .block();
    }

    public int getTotalPagesForUpcoming() {
        return extractTotalPages(getUpcomingMovies(1));
    }

    public int getTotalPagesForNowPlaying() {
        return extractTotalPages(getNowPlayingMovies(1));
    }

    public TmdbMovieSearchResponseDto searchMovies(String query, int page) {
        return tmdbWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/search/movie")
                                    .queryParam("api_key", tmdbApiKey)
                                    .queryParam("language", LANGUAGE)
                                    .queryParam("query", query)
                                    .queryParam("page", page)
                                    .build())
                            .retrieve()
                            .bodyToMono(TmdbMovieSearchResponseDto.class)
                            .block();
    }

    private int extractTotalPages(TmdbMovieListResponseDto response) {
        if (response == null || response.getTotalPages() == null) {
            return DEFAULT_TOTAL_PAGES;
        }

        return response.getTotalPages();
    }
}
