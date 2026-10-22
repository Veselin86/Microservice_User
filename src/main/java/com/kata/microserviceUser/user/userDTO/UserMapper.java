package com.kata.microserviceUser.user.userDTO;

import com.kata.microserviceUser.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse getUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }
}
