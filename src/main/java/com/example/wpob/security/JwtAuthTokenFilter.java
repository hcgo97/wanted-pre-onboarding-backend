package com.example.wpob.security;

import com.example.wpob.dto.UserTokenDto;
import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.TokenException;
import com.example.wpob.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtUtil jwtUtil;

    @Value("${api.path.default}")
    private String API_URL_PREFIX;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 다음과 같은 uri는 토큰검증x
        String[] equalsWith = {
                "/", "/webjars"
        };

        // 다음으로 시작하는 uri는 토큰검증x
        String[] startWith = {
                API_URL_PREFIX + "/users",
                "/error", "/actuator"
        };

        // 다음으로 끝나는 형식은 토큰검증x
        String[] endWith = {
                "/downloads",
                ".html", ".jpg", ".png", ".gif", ".ico", ".js", ".css"
        };

        boolean equalsWithPass = Arrays.asList(equalsWith).contains(uri);
        boolean startWithPass = Arrays.stream(startWith).anyMatch(uri::startsWith);
        boolean endWithPass = Arrays.stream(endWith).anyMatch(uri::endsWith);

        if (equalsWithPass || startWithPass || endWithPass) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("doFilterInternal");
        log.debug("[url]" + uri);

        try {
            // 토큰 검증
            String token = getToken(request);

            if (jwtUtil.validateJwtToken(token)) {
                // payload 파싱
                UserTokenDto userInfo = UserTokenDto.builder()
                        .userId(jwtUtil.getBodyValue(token, "userId").toString())
                        .email(jwtUtil.getBodyValue(token, "email").toString())
                        .token(token)
                        .build();

                log.debug("[userInfo]" + userInfo.toString());

                // 자격증명, Role 관련 사용하지 않으므로 null 처리
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userInfo, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (TokenException e) {
            log.error(e.getCode());
            log.error(e.getMessage());

            handlerExceptionResolver.resolveException(request, response, null, e);
            return;

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

        log.info("Authenticate Success");
        filterChain.doFilter(request, response);
    }

    /**
     * 토큰 유무 체크
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            throw new TokenException(ApiResultStatus.TOKEN_NOT_FOUND);
        }

        if (token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }

        return token;
    }
}
