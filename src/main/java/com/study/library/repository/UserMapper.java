package com.study.library.repository;

import com.study.library.entity.OAuth2;
import com.study.library.entity.RoleRegister;
import com.study.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public User findUserByUserName(String username);
    public int saveUser(User user);
    public RoleRegister findRoleRegistersByUserIdAndRoleId(@Param("userId") int userId, @Param("roleId") int roleId);
    public int saveRole( @Param("userId") int userId, @Param("roleId") int roleId); // sql 문이랑 키 값을 맞춰야된다.
    public User findUserByOAuth2name(String oAuth2name);
    public int saveOAuth2(OAuth2 oAuth2);
    public int modifyPassword(User user);
}
