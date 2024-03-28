package com.study.library.security.handler;

import com.study.library.entity.User;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import com.study.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.groups.Default;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${client.deploy-address}")
    private String clientAddress;


    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        System.out.println(authentication.getPrincipal().getClass()); 서비스에 있는 loadUser 리턴한 값
//        System.out.println(authentication.getName()); principal 에 있는 name 을 가져온다

        String name = authentication.getName();

        // OAuth2 로그인을 통해 회원가입이 되어있지 않은 상태
        // OAuth2 동기화
        User user = userMapper.findUserByOAuth2name(name);
        if(user == null) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String providerName = oAuth2User.getAttribute("provider").toString();

            response.sendRedirect("http://" + clientAddress + "/auth/oauth2?name=" + name +"&provider=" + providerName); // 다른 주소로 보내는 함수. 지금은 3000 포트로 보낸다
            return;
        }

        // OAuth2(소셜 로그인) 로그인을 통해 회원가입을 진행한 기록이 있는지
        // 토큰을 만들어서 로그인 한 곳에 보내야 된다.
        String accessToken = jwtProvider.generateToken(user);
        response.sendRedirect("http://" + clientAddress + "/auth/oauth2/signin?accessToken=" + accessToken);

    }
}
