package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddUserDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping(path = "/user")
public interface UserController {

    @PostMapping(path = "/add")
    ApiResult<UserDTO> add(@RequestBody AddUserDTO userDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @GetMapping("/list")
    ApiResult<List<UserDTO>> getUsers();

    @GetMapping("/{id}")
    ApiResult<UserDTO> getUser(@PathVariable Integer id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Integer id);

    @PutMapping("/{id}")
    ApiResult<UserDTO> edit(@RequestBody UserDTO userDTO,
                                @PathVariable Integer id);
}
