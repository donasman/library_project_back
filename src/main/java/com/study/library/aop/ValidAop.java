package com.study.library.aop;

import com.study.library.dto.OAuth2SignupReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.exception.ValidException;
import com.study.library.repository.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {

    @Autowired
    private UserMapper userMapper;

    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();

        Object[] args = proceedingJoinPoint.getArgs();

        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg; // bindingResult 찾아서 사용할 수 있게 다운캐스팅 시킨다.
            }
        }
        // 일반 회원가입
        if(methodName.equals("signup")) {
            SignupReqDto signupReqDto = null;
            for(Object arg : args) {
                if(arg.getClass() == SignupReqDto.class) {
                    signupReqDto = (SignupReqDto) arg; // dto 찾는 로직
                }
            }
            if(userMapper.findUserByUserName(signupReqDto.getUsername()) != null) {
                ObjectError objectError = new FieldError("username", "username", "이미 등록된 사용자이름입니다.");
                bindingResult.addError(objectError);
            }
        }

        // 소설(구글) 회원가입
        if(methodName.equals("oAuth2Signup")) {
            OAuth2SignupReqDto oAuth2SignupReqDto = null;

            for(Object arg : args) {
                if(arg.getClass() == OAuth2SignupReqDto.class) {
                    oAuth2SignupReqDto = (OAuth2SignupReqDto) arg; // dto 찾는 로직
                }
            }

            if(userMapper.findUserByUserName(oAuth2SignupReqDto.getUsername()) != null) {
                ObjectError objectError = new FieldError("username", "username", "이미 등록된 사용자이름입니다.");
                bindingResult.addError(objectError);
            }
        }

        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField(); // DTO 변수명
                String message = fieldError.getDefaultMessage(); //메세지 내용
                errorMap.put(fieldName, message); // 에레를 키 밸류로 만든다
            }
            throw new ValidException(errorMap); // 예외가 생기면 생긴 키 밸류를 보낸다
        }

        return proceedingJoinPoint.proceed();
    }
}
