package org.example.condigbat.controller.way;


import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.UserProblemDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/user-problem")
public interface UserProblemController {


    @GetMapping("/get-user-problem")
    ApiResult<UserProblemDTO> getUserProblem(@RequestParam Integer userId,
                                             @RequestParam Integer problemId);

    @GetMapping("/get-user-problems")
    ApiResult<List<UserProblemDTO>> getUserProblems();






}
