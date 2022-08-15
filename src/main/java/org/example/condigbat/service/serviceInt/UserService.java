package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.AddUserDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserDTO;

import java.util.List;

public interface UserService {

    ApiResult<UserDTO> add(AddUserDTO addUserDTO);

    ApiResult<List<UserDTO>> getUsers();

    ApiResult<UserDTO> getUser(Integer id);

    ApiResult<Boolean> delete(Integer id);

    ApiResult<UserDTO> edit(UserDTO userDTO, Integer id);

}
