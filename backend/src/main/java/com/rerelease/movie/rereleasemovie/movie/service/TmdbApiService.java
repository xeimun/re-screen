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

    private final WebClient tmdbWebClient;

    @Value("${tmdb.api.key}")  // application.properties에서 TMDB API 키 값을 주입
    private String tmdbApiKey;

    /**
     * TMDB Open API를 호출하여 개봉 예정 영화 목록을 가져옴
     *
     * @param page 요청할 페이지 번호 (1페이지당 20개 영화)
     * @return TMDB API에서 반환된 개봉 예정 영화 목록 (`TmdbMovieListResponseDto`)
     */
    public TmdbMovieListResponseDto getUpcomingMovies(int page) {
        String url = "/movie/upcoming?api_key=" + tmdbApiKey + "&language=ko-KR&region=KR&page=" + page;

        return tmdbWebClient.get()  // GET 요청 수행
                            .uri(url)  // 생성한 URL을 사용
                            .retrieve()  // 응답을 받아 처리
                            .bodyToMono(TmdbMovieListResponseDto.class)  // 응답 바디를 `TmdbMovieListResponseDto`로 변환
                            .block();  // 동기 방식으로 결과를 반환 (비동기 처리 시 block() 제거 필요)
    }

    /**
     * TMDB Open API를 호출하여 현재 상영 중인 영화 목록을 가져옴
     *
     * @param page 요청할 페이지 번호 (1페이지당 20개 영화)
     * @return TMDB API에서 반환된 현재 상영 중인 영화 목록 (`TmdbMovieListResponseDto`)
     */
    public TmdbMovieListResponseDto getNowPlayingMovies(int page) {  // 추가된 부분
        String url = "/movie/now_playing?api_key=" + tmdbApiKey + "&language=ko-KR&region=KR&page=" + page;

        return tmdbWebClient.get()
                            .uri(url)
                            .retrieve()
                            .bodyToMono(TmdbMovieListResponseDto.class)
                            .block();
    }

    /**
     * TMDB API에서 전체 개봉 예정 영화의 총 페이지 수를 가져옴
     *
     * @return 총 페이지 수 (`totalPages` 값), 오류 발생 시 기본값 1 반환
     */
    public int getTotalPagesForUpcoming() {
        TmdbMovieListResponseDto response = getUpcomingMovies(1);  // 첫 번째 페이지 데이터를 요청하여 총 페이지 수 확인
        if (response != null) {
            return response.getTotalPages();
        }
        return 1;  // 응답이 null이면 기본값 1 반환
    }

    /**
     * TMDB API에서 현재 개봉 영화의 총 페이지 수를 가져옴
     *
     * @return 총 페이지 수 (`totalPages` 값), 오류 발생 시 기본값 1 반환
     */
    public int getTotalPagesForNowPlaying() {
        TmdbMovieListResponseDto response = getNowPlayingMovies(1);
        if (response != null) {
            return response.getTotalPages();
        }
        return 1;
    }

    /**
     * TMDB API에서 특정 영화 제목으로 검색합니다.
     *
     * @param query 검색할 영화 제목 (예: "인셉션")
     * @param page  검색 결과의 페이지 번호 (1부터 시작)
     * @return TMDB 검색 결과를 담은 DTO (TmdbMovieSearchResponseDto)
     */
    public TmdbMovieSearchResponseDto searchMovies(String query, int page) {
        String url = "/search/movie?api_key=" + tmdbApiKey
                + "&language=ko-KR"
                + "&query=" + query
                + "&page=" + page;

        return tmdbWebClient.get()
                            .uri(url)
                            .retrieve()
                            .bodyToMono(TmdbMovieSearchResponseDto.class)
                            .block();
    }

}
