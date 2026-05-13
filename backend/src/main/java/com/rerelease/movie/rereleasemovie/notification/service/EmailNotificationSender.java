package com.rerelease.movie.rereleasemovie.notification.service;

import com.rerelease.movie.rereleasemovie.alert.entity.UserMovieAlert;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationSender {

    private final JavaMailSender mailSender;

    public void send(UserMovieAlert alert) throws MessagingException {
        String movieTitle = alert.getMovieTitle();
        String searchKeyword = "영화 " + movieTitle;
        String encodedTitle = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        String naverSearchUrl = "https://search.naver.com/search.naver?query=" + encodedTitle;
        String imageUrl = "https://image.tmdb.org/t/p/w500" + alert.getPosterPath();
        String subject = "(재)개봉 알림: " + movieTitle;

        String htmlContent = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>🎬 영화 (재)개봉 알림 🎉</h2>
                    <p><strong>“%s”</strong>의 (재)개봉 소식을 전해드립니다!</p>
                    <img src="%s" alt="포스터 이미지" style="max-width:300px; border-radius:8px; margin:20px 0;" />
                    <p>
                        <a href="%s" target="_blank" style="color: #1e90ff; text-decoration: none; font-weight: bold;">
                            네이버에서 "%s" 검색하기 🔍
                        </a>
                    </p>
                </div>
                """.formatted(movieTitle, imageUrl, naverSearchUrl, movieTitle);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(alert.getUser()
                          .getEmail());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}
