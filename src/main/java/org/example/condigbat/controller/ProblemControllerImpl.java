package org.example.condigbat.controller;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.controller.way.ProblemController;
import org.example.condigbat.payload.AddProblemDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.ProblemDTO;
import org.example.condigbat.service.serviceInt.ProblemService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProblemControllerImpl implements ProblemController {

    private final ProblemService problemService;

    @Override
    public ApiResult<ProblemDTO> add(AddProblemDTO addProblemDTO) {
        return problemService.add(addProblemDTO);
    }

    @Override
    public ApiResult<List<ProblemDTO>> getProblems() {
        return problemService.getProblems();
    }

    @Override
    public ApiResult<ProblemDTO> getProblem(Integer id) {
        return problemService.getProblem(id);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        return problemService.delete(id);
    }

    @Override
    public ApiResult<ProblemDTO> edit(ProblemDTO problemDTO, Integer id) {
        return problemService.edit(problemDTO,id);
    }

}
