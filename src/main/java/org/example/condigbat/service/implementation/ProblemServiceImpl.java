package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Problem;
import org.example.condigbat.entity.Section;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.repository.ProblemRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.ProblemService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository repository;

    private final UserProblemRepository userProblemRepository;

    private final SectionRepository sectionRepository;


    @Override
    public ApiResult<ProblemDTO> add(@NotNull(message = "Problem must be not null") AddProblemDTO addProblemDTO) {

        if (addProblemDTO.getTitle() == null)
            throw RestException.restThrow("title is empty please enter the title", HttpStatus.BAD_REQUEST);
        if (addProblemDTO.getSectionDTO() == null||addProblemDTO.getSectionDTO().getId() == null)
            throw RestException.restThrow("section id or section is empty please enter the title",HttpStatus.BAD_REQUEST);

        Optional<Problem> titleAndId = repository.getProblemByTitleAndSectionId(
                addProblemDTO.getTitle(),
                addProblemDTO.getSectionDTO().getId());

        if (titleAndId.isPresent())
            throw RestException.restThrow(addProblemDTO.getTitle() + " already exists in "
                    + titleAndId.get().getSection().getTitle(),HttpStatus.BAD_REQUEST);

        sectionRepository.findById(addProblemDTO.getSectionDTO().getId()).orElseThrow(()->
                RestException.restThrow(addProblemDTO.getSectionDTO().getId() + " id section not found"
                ,HttpStatus.NOT_FOUND));

        Section byId1 = sectionRepository.findById(addProblemDTO.getSectionDTO().getId()).orElseThrow(
                ()->RestException.restThrow("Error",HttpStatus.NOT_FOUND)
        );


        Problem problem = new Problem();
        problem.setTitle(addProblemDTO.getTitle());
        problem.setSection(byId1);
        problem.setDescription(addProblemDTO.getDescription());
        problem.setMethodSignature(addProblemDTO.getMethodSignature());
        repository.save(problem);

       Problem problemByTitleAndSectionId = repository.getProblemByTitleAndSectionId(
               problem.getTitle(), problem.getSection().getId()).orElseThrow(()->
                    RestException.restThrow("Error",HttpStatus.NOT_FOUND)
               );

        return ApiResult.successResponse(problemToProblemDTO(problemByTitleAndSectionId));
    }

    @Override
    public ApiResult<List<ProblemDTO>> getProblems() {
        List<ProblemDTO> res = new ArrayList<>();

        for (Problem problem : repository.findAll()) {
            ProblemDTO problemDTO = problemToProblemDTO(problem);
            problemDTO.setSolutionCount(userProblemRepository.countAllBySolvedAndProblem_Id(true, Long.valueOf(problem.getId())));
            problemDTO.setTryCount(userProblemRepository.countAllByProblem_Id(Long.valueOf(problem.getId())));

        }

        return ApiResult.successResponse(res);
    }

    @Override
    public ApiResult<ProblemDTO> getProblem(@NotNull(message = "id must be not null") Integer id) {
        Problem problem = repository.findById(id).orElseThrow(() ->
                RestException.restThrow("This id problem not found", HttpStatus.NOT_FOUND));

        long tryCount = userProblemRepository.countAllByProblem_Id(Long.valueOf(problem.getId()));
        long solvedCount = userProblemRepository.countAllBySolvedAndProblem_Id(true, Long.valueOf(problem.getId()));
        ProblemDTO problemDTO = problemToProblemDTO(problem);
        problemDTO.setTryCount(tryCount);
        problemDTO.setSolutionCount(solvedCount);
        return ApiResult.successResponse(problemDTO);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        repository.findById(id).orElseThrow(
                () -> RestException.restThrow("id not found", HttpStatus.BAD_REQUEST)
        );
        repository.deleteById(id);
        return ApiResult.successResponse("success deleted", true);
    }

    @Override
    public ApiResult<ProblemDTO> edit(@NotNull(message = "problem must be not null") ProblemDTO problemDTO,@NotNull(message = "problem id must be not null") Integer id) {
        Optional<Problem> sect = repository.getProblemByTitleAndSectionId(problemDTO.getTitle(),
                problemDTO.getSectionDTO().getId());

        Problem empty = repository.findById(id).orElseThrow(()-> RestException.restThrow(
                id + " id is not found  first create then update",
                HttpStatus.BAD_REQUEST
        ));


        if(problemDTO.getSectionDTO() == null||problemDTO.getSectionDTO().getId()==null)
            throw RestException.restThrow("please enter section id or section",HttpStatus.BAD_REQUEST);

        Section section = sectionRepository.findById(problemDTO.getSectionDTO().getId()).orElseThrow(()->
                RestException.restThrow("section id not found",HttpStatus.NOT_FOUND)
                );

        if (sect.isPresent() && !Objects.equals(id, sect.get().getId()))
            throw RestException.restThrow(problemDTO.getTitle() + " already exists",HttpStatus.BAD_REQUEST);

        Problem problem = new Problem();
        problem.setId(id);
        problem.setTitle(problemDTO.getTitle()==null?
                empty.getTitle():
                problemDTO.getTitle()
        );
        problem.setDescription(problemDTO.getDescription()==null?empty.getDescription():problemDTO.getDescription());
        problem.setSection(problemDTO.getSectionDTO()==null?empty.getSection():section);
        problem.setMethodSignature(problemDTO.getMethodSignature()==null?empty.getMethodSignature():problemDTO.getMethodSignature());
        repository.save(problem);
        return ApiResult.successResponse(problemToProblemDTO(problem));
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
}
