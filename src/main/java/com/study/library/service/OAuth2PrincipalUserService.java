package com.study.library.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OAuth2PrincipalUserService implements OAuth2UserService {


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest); // 내가 받아온 유저정보(userRequest)를 가져온다.

        Map<String, Object> attributes = oAuth2User.getAttributes(); // 1. 기존의 유저 정보로
        String provider = userRequest.getClientRegistration().getClientName(); //ex) 구글, 카카오, 네이버

        System.out.println(attributes);
        Map<String, Object> newAttributes = null;
        String id = null;
        switch (provider) {
            case "Google":
                id = attributes.get("sub").toString();
                break;
            case "Naver":
                Map<String,Object> response = (Map<String,Object>) attributes.get("response");
                id = response.get("id").toString();
                break;
            case "Kakao":
                id = attributes.get("id").toString();
                break;
        }
        newAttributes = Map.of("id", id, "provider", provider);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), newAttributes, "id"); // 2. 새로운 유저를 만든다
        // (권한(없어도됨), id provide 들어있는 map, getName 사용할 키)
    }
}
