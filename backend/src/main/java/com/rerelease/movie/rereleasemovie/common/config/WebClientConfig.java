package com.rerelease.movie.rereleasemovie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient koficWebClient() {
        return WebClient.builder()
                        .baseUrl("http://kobis.or.kr/kobisopenapi/webservice/rest")
                        .build();
    }

    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                        .baseUrl("https://api.themoviedb.org/3")
                        .build();
    }
}
