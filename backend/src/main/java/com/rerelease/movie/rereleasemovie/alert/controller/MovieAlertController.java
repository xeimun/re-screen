package com.rerelease.movie.rereleasemovie.alert.controller;

import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertRequest;
import com.rerelease.movie.rereleasemovie.alert.dto.MovieAlertResponse;
import com.rerelease.movie.rereleasemovie.alert.dto.UserAlertManageDto;
import com.rerelease.movie.rereleasemovie.alert.service.MovieAlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class MovieAlertController {

    private final MovieAlertService movieAlertService;

    @PostMapping("/register")
    public ResponseEntity<MovieAlertResponse> registerMovieAlert(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestBody MovieAlertRequest request) {

        MovieAlertResponse response = movieAlertService.registerMovieAlert(getUsername(userDetails), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-alerts")
    public ResponseEntity<List<UserAlertManageDto>> getUserMovieAlerts(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        List<UserAlertManageDto> alerts = movieAlertService.getUserMovieAlerts(getUsername(userDetails));
        return ResponseEntity.ok(alerts);
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteUserMovieAlert(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @PathVariable Long alertId) {

        movieAlertService.deleteUserMovieAlert(getUsername(userDetails), alertId);
        return ResponseEntity.noContent()
                             .build();
    }

    private String getUsername(org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails == null) {
            throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        }

        return userDetails.getUsername();
    }
}
