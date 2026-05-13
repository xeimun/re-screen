package com.rerelease.movie.rereleasemovie.movie.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@DisplayName("TMDB API 서비스")
class TmdbApiServiceTest {

    private static final String MOVIE_LIST_RESPONSE = """
            {
              "page": 1,
              "total_pages": 7,
              "total_results": 1,
              "results": []
            }
            """;

    private AtomicReference<URI> requestedUri;
    private TmdbApiService tmdbApiService;

    @BeforeEach
    void setUp() {
        requestedUri = new AtomicReference<>();
        WebClient webClient = WebClient.builder()
                                       .baseUrl("https://api.themoviedb.org/3")
                                       .exchangeFunction(request -> {
                                           requestedUri.set(request.url());
                                           return Mono.just(jsonResponse(MOVIE_LIST_RESPONSE));
                                       })
                                       .build();

        tmdbApiService = new TmdbApiService(webClient);
        ReflectionTestUtils.setField(tmdbApiService, "tmdbApiKey", "tmdb-key");
    }

    @Test
    @DisplayName("개봉 예정 영화 조회 시 경로와 공통 query parameter를 설정한다")
    void getUpcomingMoviesBuildsRequestUri() {
        tmdbApiService.getUpcomingMovies(2);

        assertThat(requestedUri.get().getPath()).isEqualTo("/3/movie/upcoming");
        assertThat(requestedUri.get().getRawQuery())
                .contains("api_key=tmdb-key")
                .contains("language=ko-KR")
                .contains("region=KR")
                .contains("page=2");
    }

    @Test
    @DisplayName("현재 상영 영화 조회 시 경로와 공통 query parameter를 설정한다")
    void getNowPlayingMoviesBuildsRequestUri() {
        tmdbApiService.getNowPlayingMovies(3);

        assertThat(requestedUri.get().getPath()).isEqualTo("/3/movie/now_playing");
        assertThat(requestedUri.get().getRawQuery())
                .contains("api_key=tmdb-key")
                .contains("language=ko-KR")
                .contains("region=KR")
                .contains("page=3");
    }

    @Test
    @DisplayName("영화 검색 시 검색어를 query parameter로 전달한다")
    void searchMoviesBuildsRequestUri() {
        tmdbApiService.searchMovies("인셉션", 4);

        assertThat(requestedUri.get().getPath()).isEqualTo("/3/search/movie");
        assertThat(requestedUri.get().getRawQuery())
                .contains("api_key=tmdb-key")
                .contains("language=ko-KR")
                .contains("query=%EC%9D%B8%EC%85%89%EC%85%98")
                .contains("page=4");
    }

    @Test
    @DisplayName("총 페이지 수는 첫 페이지 응답의 total_pages 값을 반환한다")
    void getTotalPagesForUpcomingReturnsResponseTotalPages() {
        int totalPages = tmdbApiService.getTotalPagesForUpcoming();

        assertThat(totalPages).isEqualTo(7);
    }

    private ClientResponse jsonResponse(String body) {
        return ClientResponse.create(HttpStatus.OK)
                             .header("Content-Type", "application/json")
                             .body(body)
                             .build();
    }
}
