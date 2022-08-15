package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.AddProblemDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.ProblemDTO;

import java.util.List;

public interface ProblemService {
    ApiResult<ProblemDTO> add(AddProblemDTO addProblemDTO);

    ApiResult<List<ProblemDTO>> getProblems();

    ApiResult<ProblemDTO> getProblem(Integer id);

    ApiResult<Boolean> delete(Integer id);

    ApiResult<ProblemDTO> edit(ProblemDTO problemDTO, Integer id);
}
