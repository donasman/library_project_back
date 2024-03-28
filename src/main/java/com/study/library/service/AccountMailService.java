package com.study.library.service;

import com.study.library.entity.RoleRegister;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import com.study.library.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class AccountMailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.address}")
    private String fromMailAddress;
    @Value("${server.deploy-address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    public boolean sendAuthMail() {
        boolean result = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalUser principalUser =(PrincipalUser) authentication.getPrincipal();
        String toMailAddress = principalUser.getEmail(); // 위 3줄 코드가 사용자의 메일을 가져온다.
        int userId = principalUser.getUserId();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage(); //mime 메세지 객체를 생성한다.
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");  //helper 메세지 객체를 넣는다. 파일을 보낼 경우에 true 변경.
            helper.setSubject("도서관리 시스템 사용자 메일 인증"); //메일 제목
            helper.setFrom(fromMailAddress); // 보내는 사람 메일 주소 설정
            helper.setTo(toMailAddress); // 받는 사람 메일 주소 설정

            String authMailToken = jwtProvider.generateAuthMailToken(userId ,toMailAddress);

            StringBuilder mailContent = new StringBuilder();
            mailContent.append("<div>");
            mailContent.append("<h1>계정 활성화 절차</h1>");
            mailContent.append("<h3>해당 계정 인증하기 위해 아래의 버튼을 클릭해 주세요</h3>");
            mailContent.append("<a href=\"http://" + serverAddress + ":" + serverPort +  "/mail/authenticate?authToken=" + authMailToken + "\" style=\"border: 1px solid #dbdbdb; padding: 10px 20px; text-decoration: none; background-color: white; color: #222222;\">인증하기</a>");
            // http://localhost:8080/mail/authenticate
            // ?authToken=jwt토큰
            mailContent.append("</div>");

            mimeMessage.setText(mailContent.toString(), "utf-8", "html");

            javaMailSender.send(mimeMessage); //메일 전송
            result = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String ,Object> authenticate(String token) {
        Claims claims = null;
        Map<String, Object> resultMap = null;
        // ExpiredJwtException => 토큰 유효기간 만료
        // MalformedJwtException => 위조, 변조
        // SignatureException => 형식, 길이 오류
        // IllegalArgumentException => null 또는 빈값
        try {
            claims = jwtProvider.getClaims(token);
            int userId = Integer.parseInt(claims.get("userId").toString());
            RoleRegister roleRegister = userMapper.findRoleRegistersByUserIdAndRoleId(userId, 2); // DB 권한 중복확인
            if(roleRegister != null) {
                resultMap = Map.of("status", false, "message", "이미 인증된 메일입니다.");
            } else {
                userMapper.saveRole(userId, 2);
                resultMap = Map.of("status", true, "message", "인증 완료.");
            }
        } catch (ExpiredJwtException e) {
            resultMap = Map.of("status", false, "message", "인증 시간을 초과하였습니다 \n 인증 메일을 다시 받으세요.");
        } catch (JwtException e) {
            resultMap = Map.of("status", false, "message", "인증 메일을 다시 받으세요. \n 인증 메일을 다시 받으세요.");
        }


        return resultMap;
    }
}
