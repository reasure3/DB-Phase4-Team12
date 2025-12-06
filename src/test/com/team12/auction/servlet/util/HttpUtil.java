package com.team12.auction.servlet.util;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpUtil {
    /**
     * Map을 form-urlencoded 형식으로 변환
     */
    public static HttpRequest.BodyPublisher buildFormDataFromMap(Map<String, String> data) {
        String formData = data.entrySet()
                .stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        return HttpRequest.BodyPublishers.ofString(formData);
    }

    /**
     * 응답에서 세션 쿠키 추출 및 저장
     */
    public static Optional<String> getCookieFromResponse(HttpResponse<String> response) {
        String cookie = response.headers().firstValue("Set-Cookie").orElse("");
        if (!cookie.isBlank()) {
            if (cookie.contains("JSESSIONID")) {
                return Optional.of(cookie.split(";")[0]);
            }
        }
        return Optional.empty();
    }
}
