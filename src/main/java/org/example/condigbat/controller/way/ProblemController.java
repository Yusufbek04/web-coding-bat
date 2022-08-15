package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddProblemDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.ProblemDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping(path = "/problem")
public interface  ProblemController {

    @PostMapping(path = "/add")
    ApiResult<ProblemDTO> add(@RequestBody AddProblemDTO addProblemDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @GetMapping("/list")
    ApiResult<List<ProblemDTO>> getProblems();

    @GetMapping("/{id}")
    ApiResult<ProblemDTO> getProblem(@PathVariable Integer id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Integer id);

    @PutMapping("/{id}")
    ApiResult<ProblemDTO> edit(@RequestBody ProblemDTO problemDTO,
                                @PathVariable Integer id);
}
