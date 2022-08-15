package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddCaseDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.CaseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping(path = "/cases")
public interface CasesController {

    @PostMapping(path = "/add")
    ApiResult<CaseDTO> add(@RequestBody AddCaseDTO caseDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @GetMapping("/list")
    ApiResult<List<CaseDTO>> getCases();

    @GetMapping("/{id}")
    ApiResult<CaseDTO> getCase(@PathVariable Long id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Long id);

    @PutMapping("/{id}")
    ApiResult<CaseDTO> edit(@RequestBody CaseDTO caseDTO,
                                @PathVariable Long id);
}
