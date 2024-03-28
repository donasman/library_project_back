package com.study.library.service;

import com.study.library.dto.OAuth2MergeDto;
import com.study.library.dto.OAuth2SignupReqDto;
import com.study.library.dto.SigninReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.entity.OAuth2;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.net.Authenticator;

@Service
public class AuthService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;


    public boolean isDuplicatedByUserName(String username) {
        return userMapper.findUserByUserName(username) != null;
    }

    @Transactional(rollbackFor = Exception.class) // 하나라도 예외가 생기면 rollback 해라
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0;
        User user = signupReqDto.toEntity(passwordEncoder);
        successCount += userMapper.saveUser(user);
        successCount += userMapper.saveRole(user.getUserId(), 1);

        if(successCount < 2) {
            throw new SaveException();
        }


    }
    @Transactional(rollbackFor = Exception.class) // 하나라도 예외가 생기면 rollback 해라
    public void oAuth2signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        int successCount = 0;
        User user = oAuth2SignupReqDto.toEntity(passwordEncoder);
        successCount += userMapper.saveUser(user);
        successCount += userMapper.saveRole(user.getUserId(), 1);
        successCount += userMapper.saveOAuth2(oAuth2SignupReqDto.toOAuth2Entity(user.getUserId()));

        if(successCount < 3) {
            throw new SaveException();
        }


    }
        public String signin(SigninReqDto signinReqDto) {
            User user = userMapper.findUserByUserName(signinReqDto.getUsername());
            if(user == null) {
                throw new UsernameNotFoundException("사용자 정보를 확인하세요");
            }
            if (!passwordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("사용자 정보를 확인하세요.");
            }

//            Authentication authentication = new UsernamePasswordAuthenticationToken(user.toPrincipalUser(), "");

            return jwtProvider.generateToken(user);
        }

        public void oAuthMerge(OAuth2MergeDto oAuth2MergeDto) {
            User user = userMapper.findUserByUserName(oAuth2MergeDto.getUsername());

            if(user == null) { // 아이디 체크
                throw new UsernameNotFoundException("사용자 정보를 확인하세요");
            }
            if (!passwordEncoder.matches(oAuth2MergeDto.getPassword(), user.getPassword())) { // 비밀번호 체크
                throw new BadCredentialsException("사용자 정보를 확인하세요.");
            }
            OAuth2 oAuth2 = OAuth2.builder()
                            .oAuth2Name(oAuth2MergeDto.getOauth2Name())
                            .userId(user.getUserId())
                            .providerName(oAuth2MergeDto.getProviderName())
                            .build();
            userMapper.saveOAuth2(oAuth2);


        }
}
