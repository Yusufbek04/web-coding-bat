package org.example.condigbat.controller;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.controller.way.UserProblemController;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserProblemDTO;
import org.example.condigbat.service.serviceInt.UserProblemService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserProblemControllerImpl implements UserProblemController {


    private final UserProblemService service;

    @Override
    public ApiResult<UserProblemDTO> getUserProblem(Integer userId, Integer problemId) {
        return service.getUserProblem(userId,problemId);
    }

    @Override
    public ApiResult<List<UserProblemDTO>> getUserProblems() {
        return service.getUserProblems();
    }
}
