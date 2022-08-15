package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.AddCaseDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.CaseDTO;

import java.util.List;

public interface CasesService {
    ApiResult<CaseDTO> add(AddCaseDTO addCaseDTO);

    ApiResult<List<CaseDTO>> getCases();

    ApiResult<CaseDTO> getCase(Long id);

    ApiResult<Boolean> delete(Long id);

    ApiResult<CaseDTO> edit(CaseDTO languageDTO, Long id);
}
