package com.rerelease.movie.rereleasemovie.movie.service;

import com.rerelease.movie.rereleasemovie.movie.dto.kofic.MovieInfoDto;
import com.rerelease.movie.rereleasemovie.movie.dto.kofic.MovieListResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KoficApiService {

    private final WebClient koficWebClient;

    @Value("${kofic.api.key}")
    private String koficApiKey;

    public List<MovieInfoDto> searchMoviesByName(String movieName) {
        MovieListResponseDto response = koficWebClient.get()
                                                      .uri(uriBuilder -> uriBuilder
                                                              .path("/movie/searchMovieList.json")
                                                              .queryParam("key", koficApiKey)
                                                              .queryParam("movieNm", movieName)
                                                              .build())
                                                      .retrieve()
                                                      .bodyToMono(MovieListResponseDto.class)
                                                      .block();

        if (response == null || response.getMovieList()
                                        .isEmpty()) {
            return List.of();
        }

        return response.getMovieList();
    }
}
