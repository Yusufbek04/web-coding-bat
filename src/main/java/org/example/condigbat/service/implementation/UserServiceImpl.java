package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.User;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.AddUserDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserDTO;
import org.example.condigbat.repository.UserRepository;
import org.example.condigbat.service.serviceInt.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public ApiResult<UserDTO> add(@Valid AddUserDTO userDTO) {

        Optional<User> userByEmail = repository.getUserByEmail(userDTO.getEmail());
        if (userByEmail.isPresent())
            throw RestException.restThrow(
                    userDTO.getEmail() + " email already registered",
                    HttpStatus.BAD_REQUEST);

        return ApiResult.successResponse(userToUserDTO(
                repository.save(userDTOToUser(new UserDTO(userDTO.getEmail(),
                        userDTO.getPassword())))));
    }

    @Override
    public ApiResult<List<UserDTO>> getUsers() {
        List<User> all = repository.findAll();
        return ApiResult.successResponse(userToUserDTO(all));
    }

    @Override
    public ApiResult<UserDTO> getUser(Integer id) {
        User byId = repository.findById(id).orElseThrow(()->
                RestException.restThrow(id + " id user not found",HttpStatus.NOT_FOUND)
                );
        return ApiResult.successResponse(userToUserDTO(byId));
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        repository.findById(id).orElseThrow(()->
                RestException.restThrow(id + " user id not found",HttpStatus.NOT_FOUND)
                );
        repository.deleteById(id);
        return ApiResult.successResponse(true);
    }

    @Override
    public ApiResult<UserDTO> edit(@NotNull(message = "user must be not null") UserDTO userDTO, Integer id) {

        User byId = repository.findById(id).orElseThrow(()->
                RestException.restThrow(id + " id user not found first create then update",
                        HttpStatus.NOT_FOUND)
                );
        if (userDTO.getId() == null)
            userDTO.setId(byId.getId());
        if (userDTO.getEmail() == null)
            userDTO.setEmail(byId.getEmail());
        if (userDTO.getPassword() == null)
            userDTO.setPassword(byId.getPassword());
        if (userDTO.getRoleEnum()==null)
            userDTO.setRoleEnum(byId.getRole());

        Optional<User> userByEmail = repository.getUserByEmail(userDTO.getEmail());
        if (userByEmail.isPresent()&& !Objects.equals(userByEmail.get().getId(), id))
            throw RestException.restThrow(userDTO.getEmail() + " already exists",HttpStatus.BAD_REQUEST);

        repository.save(userDTOToUser(userDTO));

        return ApiResult.successResponse(userDTO);
    }


    private UserDTO userToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoleEnum(user.getRole());
        return userDTO;
    }

    private List<UserDTO> userToUserDTO(List<User> users) {
        List<UserDTO> res = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(user.getPassword());
            userDTO.setRoleEnum(user.getRole());
            res.add(userDTO);
        }
        return res;
    }

    private User userDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRoleEnum());
        return user;
    }
}
