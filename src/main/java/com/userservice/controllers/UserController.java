package com.userservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dtos.*;
import com.userservice.exception.InvalidPasswordException;
import com.userservice.exception.InvalidTokenException;
import com.userservice.models.Token;
import com.userservice.models.User;
import com.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signUp( @RequestBody SignUpRequestDto requestDto){
        User user= userService.signUp(

            requestDto.getEmail(),
            requestDto.getPassword(),
                requestDto.getName()
        );
                //get user dto from user
        return UserDto.from(user);
    }

    @PostMapping("/login") // localhost:8080/users/login
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) throws InvalidPasswordException {
        Token token = userService.login(requestDto.getEmail(),
                requestDto.getPassword());

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);
        return responseDto;
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout( @RequestBody LogoutRequestDto requestDto) throws InvalidTokenException{
        ResponseEntity<Void> responseEntity = null;
        try {
            userService.logout(requestDto.getToken());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Something went wrong");
            responseEntity=new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
