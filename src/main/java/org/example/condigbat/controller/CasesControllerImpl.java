package org.example.condigbat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.condigbat.controller.way.CasesController;
import org.example.condigbat.payload.AddCaseDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.CaseDTO;
import org.example.condigbat.service.serviceInt.CasesService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CasesControllerImpl implements CasesController {


    private final CasesService casesService;

    @Override
    public ApiResult<CaseDTO> add(AddCaseDTO caseDto) {
        log.info("Add method entered: {}", caseDto);

        ApiResult<CaseDTO> result = casesService.add(caseDto);

        log.info("Add method exited: {}", result);

        return result;
    }

    @Override
    public ApiResult<List<CaseDTO>> getCases() {
        return casesService.getCases();
    }

    @Override
    public ApiResult<CaseDTO> getCase(Long id) {
        return casesService.getCase(id);
    }

    @Override
    public ApiResult<Boolean> delete(Long id) {
        return casesService.delete(id);
    }

    @Override
    public ApiResult<CaseDTO> edit(CaseDTO caseDto, Long id) {
        return casesService.edit(caseDto,id);
    }
}
