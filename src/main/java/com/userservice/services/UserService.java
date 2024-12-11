package com.userservice.services;

import com.userservice.exception.InvalidPasswordException;
import com.userservice.models.Token;
import com.userservice.models.User;
import com.userservice.repositories.TokenRepository;
import com.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public User signUp(String email, String password,String name) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            //user is always present in db so no need to sign up
            return optionalUser.get();
        }
        //user is not present so we need to create new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Token login(String email, String password) throws InvalidPasswordException {
        /*
        1. Check if the use exists with the given email or not.
        2. If not, throw an exception or redirect the user to signup.
        3. If yes, then compare the incoming password with the password stored in the DB.
        4. If password matches then login successful and return new token.
         */
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            //User with given email isn't present in DB.
            return null;
        }

        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            //throw an exception
            throw new InvalidPasswordException("Please enter correct password");
        }

        //Login successful, generate a new token.
        Token token = generateToken(user);

        return tokenRepository.save(token);
    }

    private Token generateToken(User user) {
        LocalDate currentTime = LocalDate.now(); // current time.
        LocalDate thirtyDaysFromCurrentTime = currentTime.plusDays(30);

        Date expiryDate = Date.from(thirtyDaysFromCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setExpiryAt(expiryDate);

        //Token value is a randomly generated String of 128 characters.
        //used apache common libs in pom.xml
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        token.setUser(user);
        return token;
    }


    public void logout(String tokenValue) throws InvalidPasswordException {
        //validate if the given token is present in db as well as is_deleted=false
    Optional<Token> optionalToken=tokenRepository.findByValueAndDeleted(tokenValue,false);

        if(optionalToken.isEmpty()){
            //Throw an Exception
           throw new InvalidPasswordException("Invalid Token Passed");
        }
        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);

        return;
    }

}
