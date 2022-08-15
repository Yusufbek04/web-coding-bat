package org.example.condigbat.controller;


import lombok.RequiredArgsConstructor;
import org.example.condigbat.controller.way.UserController;
import org.example.condigbat.payload.AddUserDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserDTO;
import org.example.condigbat.service.serviceInt.UserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;

    @Override
    public ApiResult<UserDTO> add(AddUserDTO userDTO) {

        return userService.add(userDTO);
    }

    @Override
    public ApiResult<List<UserDTO>> getUsers() {
        return userService.getUsers();
    }

    @Override
    public ApiResult<UserDTO> getUser(Integer id) {
        return userService.getUser(id);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        return userService.delete(id);
    }

    @Override
    public ApiResult<UserDTO> edit(UserDTO userDTO, Integer id) {
        return userService.edit(userDTO,id);
    }

}
