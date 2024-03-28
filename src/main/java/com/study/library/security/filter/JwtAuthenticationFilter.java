package com.study.library.security.filter;

import com.study.library.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends GenericFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest; // HttpServletRequest 사용하기 위해 다운캐스팅
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Boolean isPermitAll = (Boolean) request.getAttribute("isPermitAll");

        if(!isPermitAll) {
            String accessToken = request.getHeader("Authorization"); //로컬스토리지에 저장된 토큰 가져오기
            String removedBearerToken = jwtProvider.removeBearer(accessToken); // bearer 지우기
            Claims claims = null;
            try {
                claims =  jwtProvider.getClaims(removedBearerToken); // 토큰 인증하기
            }catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); //인증실패 (401)
                return;
            }
            Authentication authentication = jwtProvider.getAuthentication(claims); // 토큰이 있는걸 확인했으니 DB에 저장되어있는 유효한 토큰인지 확인

            if(authentication == null) { // null 이면 토큰은 있지만 DB에 없는 유효하지 않는 토큰
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); //인증실패
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication); //인증이 완료된 토큰은 set 해줌
        }
        // doFilter 위 전처리 영역
        filterChain.doFilter(request, response);
        // doFilter 밑 후처리 영역
    }
}
