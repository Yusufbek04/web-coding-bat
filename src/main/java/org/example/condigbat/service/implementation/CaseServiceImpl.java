package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Case;
import org.example.condigbat.entity.Problem;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.repository.CaseRepository;
import org.example.condigbat.repository.ProblemRepository;
import org.example.condigbat.service.serviceInt.CasesService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CasesService {

    private final CaseRepository repository;

    private final ProblemRepository problemRepository;

    @Override
    public ApiResult<CaseDTO> add(@Valid AddCaseDTO caseDTO) {

        Optional<Case> caseByArgsAndProblemId = repository.getCaseByArgsAndProblemId(caseDTO.getArgs(), caseDTO.getProblemDTO().getId());
        if (caseByArgsAndProblemId.isPresent())
            throw RestException.restThrow(caseByArgsAndProblemId.get().getArgs() + " already exists in "
                            + caseByArgsAndProblemId.get().getProblem().getId() + " problem id"
                    , HttpStatus.BAD_REQUEST);
        Problem byId = problemRepository.findById(caseDTO.getProblemDTO().getId()).orElseThrow(
                () -> RestException.restThrow("not found problem", HttpStatus.NOT_FOUND)
        );

        caseDTO.setProblemDTO(problemToProblemDTO(byId));

        Case cases = new Case();
        cases.setArgs(caseDTO.getArgs());
        cases.setVisible(caseDTO.getVisible());
        cases.setExpected(caseDTO.getExpected());
        cases.setProblem(byId);
        repository.save(cases);

        return ApiResult.successResponse(new CaseDTO(
                caseDTO.getArgs(),
                caseDTO.getExpected(),
                caseDTO.getVisible(),
                problemToProblemDTO(byId)
        ));
    }

    @Override
    public ApiResult<List<CaseDTO>> getCases() {
        List<CaseDTO> res = new ArrayList<>();
        for (Case aCase : repository.findAll()) {
            CaseDTO caseDTO = new CaseDTO();
            caseDTO.setId(aCase.getId());
            caseDTO.setArgs(aCase.getArgs());
            caseDTO.setVisible(aCase.getVisible());
            caseDTO.setExpected(aCase.getExpected());
            caseDTO.setProblemDTO(problemToProblemDTO(aCase.getProblem()));
            res.add(caseDTO);
        }
        return ApiResult.successResponse(res);
    }

    @Override
    public ApiResult<CaseDTO> getCase(Long id) {

        Case byId = repository.findById(id).orElseThrow(() ->
                RestException.restThrow(id + " id case not found", HttpStatus.NOT_FOUND));
        return ApiResult.successResponse(caseToCaseDTO(byId));
    }

    @Override
    public ApiResult<Boolean> delete(Long id) {
        repository.findById(id).orElseThrow(() ->
                RestException.restThrow(id + " case id not found", HttpStatus.NOT_FOUND)
        );
        repository.deleteById(id);

        return ApiResult.successResponse(true);
    }

    @Override
    public ApiResult<CaseDTO> edit(CaseDTO caseDTO, Long id) {

        Case byId = repository.findById(id).orElseThrow(() ->
                RestException.restThrow(id+" case id not found", HttpStatus.NOT_FOUND)
        );
        if (caseDTO == null)
            throw RestException.restThrow("1", HttpStatus.MULTI_STATUS);
        if (caseDTO.getArgs() == null)
            caseDTO.setArgs(byId.getArgs());
        if (caseDTO.getExpected() == null)
            caseDTO.setExpected(byId.getExpected());
        if (caseDTO.getProblemDTO() == null)
            caseDTO.setProblemDTO(problemToProblemDTO(byId.getProblem()));
        if (caseDTO.getVisible() == null)
            caseDTO.setVisible(byId.getVisible());

        Problem byId1 = problemRepository.findById(caseDTO.getProblemDTO().getId()).orElseThrow(
                ()->
                RestException.restThrow(caseDTO.getProblemDTO().getId()+" problem id not found",
                HttpStatus.MULTI_STATUS)
                );

        Optional<Case> caseByArgsAndProblemId = repository.
                getCaseByArgsAndProblemId(caseDTO.getArgs(), caseDTO.getProblemDTO().getId());

        if (caseByArgsAndProblemId.isPresent())
            throw RestException.restThrow("this case already exists", HttpStatus.BAD_REQUEST);
        caseDTO.setProblemDTO(
                problemToProblemDTO(byId1)
        );
        caseDTO.setId(id);
        repository.save(caseDTOToCase(caseDTO));

        return ApiResult.successResponse(caseDTO);
    }

    Case caseDTOToCase(CaseDTO caseDTO) {
        Case aCase = new Case();
        Problem problem = new Problem();
        problem.setId(caseDTO.getProblemDTO().getId());

        aCase.setId(caseDTO.getId());
        aCase.setArgs(caseDTO.getArgs());
        aCase.setExpected(caseDTO.getExpected());
        aCase.setVisible(caseDTO.getVisible());
        aCase.setProblem(problem);
        return aCase;
    }

    private ProblemDTO problemToProblemDTO(Problem problem) {
        SectionDTO section = new SectionDTO();
        section.setId(problem.getSection().getId());
        section.setTitle(problem.getSection().getTitle());
        section.setDescription(problem.getSection().getDescription());
        section.setLanguage(CommonUtils.languageToLanguageDTO(problem.getSection().getLanguage()));
        section.setMaxRate(problem.getSection().getMaxRate());
        return new ProblemDTO(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getMethodSignature(),
                section,
                null,
                null
        );
    }

    private CaseDTO caseToCaseDTO(Case cases) {
        CaseDTO caseDTO = new CaseDTO();
        caseDTO.setId(cases.getId());
        caseDTO.setArgs(cases.getArgs());
        caseDTO.setExpected(cases.getExpected());
        caseDTO.setVisible(cases.getVisible());
        caseDTO.setProblemDTO(problemToProblemDTO(cases.getProblem()));
        return caseDTO;
    }
}

