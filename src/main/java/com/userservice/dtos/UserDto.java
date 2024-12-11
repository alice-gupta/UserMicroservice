package com.userservice.dtos;

import com.userservice.models.Role;
import com.userservice.models.User;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private List<Role> roles;
    private boolean isEmailVerified;

    public static UserDto from(User user) {//static otherwise we  have to create user object
       if(user == null) return null;

        UserDto userDto = new UserDto();
       userDto.setEmail(user.getEmail());
       userDto.setName(user.getName());
        userDto.setRoles(user.getRoles());
       userDto.setEmailVerified(user.isEmailVerified());

       return userDto;
    }

}
