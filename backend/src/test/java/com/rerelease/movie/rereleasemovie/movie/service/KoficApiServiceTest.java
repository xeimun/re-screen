package com.rerelease.movie.rereleasemovie.movie.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@DisplayName("KOFIC API 서비스")
class KoficApiServiceTest {

    private AtomicReference<URI> requestedUri;
    private KoficApiService koficApiService;

    @BeforeEach
    void setUp() {
        requestedUri = new AtomicReference<>();
        WebClient webClient = WebClient.builder()
                                       .baseUrl("http://kobis.or.kr/kobisopenapi/webservice/rest")
                                       .exchangeFunction(request -> {
                                           requestedUri.set(request.url());
                                           return Mono.just(jsonResponse());
                                       })
                                       .build();

        koficApiService = new KoficApiService(webClient);
        ReflectionTestUtils.setField(koficApiService, "koficApiKey", "kofic-key");
    }

    @Test
    @DisplayName("영화명 검색 시 KOFIC API 키와 영화명을 query parameter로 전달한다")
    void searchMoviesByNameBuildsRequestUri() {
        koficApiService.searchMoviesByName("인셉션");

        assertThat(requestedUri.get().getPath()).isEqualTo("/kobisopenapi/webservice/rest/movie/searchMovieList.json");
        assertThat(requestedUri.get().getRawQuery())
                .contains("key=kofic-key")
                .contains("movieNm=%EC%9D%B8%EC%85%89%EC%85%98");
    }

    private ClientResponse jsonResponse() {
        return ClientResponse.create(HttpStatus.OK)
                             .header("Content-Type", "application/json")
                             .body("""
                                     {
                                       "movieListResult": {
                                         "movieList": []
                                       }
                                     }
                                     """)
                             .build();
    }
}
