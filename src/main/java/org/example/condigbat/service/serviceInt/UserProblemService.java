package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserProblemDTO;

import java.util.List;

public interface UserProblemService {

    ApiResult<UserProblemDTO> getUserProblem(Integer userId,Integer problemId);

    ApiResult<List<UserProblemDTO>> getUserProblems();



}
