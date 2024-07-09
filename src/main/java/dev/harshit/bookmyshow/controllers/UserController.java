package dev.harshit.bookmyshow.controllers;

import dev.harshit.bookmyshow.dtos.ResponseStatus;
import dev.harshit.bookmyshow.dtos.SignUpRequestDto;
import dev.harshit.bookmyshow.dtos.SignUpResponseDto;
import dev.harshit.bookmyshow.models.User;
import dev.harshit.bookmyshow.services.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = new SignUpResponseDto();
        User user = null;

        try {
            user = userService.signUp(
                    requestDto.getEmail(),
                    requestDto.getPassword()
            );

            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
            responseDto.setUserId(user.getId());
        } catch (Exception ex) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }

        return responseDto;
    }
}
