package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.entity.Section;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.AddSectionDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.SectionDTO;
import org.example.condigbat.repository.LanguageRepository;
import org.example.condigbat.repository.ProblemRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.SectionService;
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
public class SectionServiceImp implements SectionService {

    private final SectionRepository repository;
    private final LanguageRepository languageRepository;

    private final ProblemRepository problemRepository;

    private final UserProblemRepository userProblemRepository;


    @Override
    public ApiResult<SectionDTO> add(AddSectionDTO addSectionDTO) {
        Optional<Section> byTitleAndId = repository.getSectionByTitleAndLanguageId(addSectionDTO.getTitle(), addSectionDTO.getLanguage().getId());

        if (byTitleAndId.isPresent())
            throw RestException.restThrow(addSectionDTO.getTitle() + " already exists in " + byTitleAndId.get().getLanguage().getTitle(), HttpStatus.BAD_REQUEST);

        Optional<Language> byId = languageRepository.findById(addSectionDTO.getLanguage().getId());
        if (byId.isEmpty())
            throw RestException.restThrow(addSectionDTO.getLanguage().getId() + " id not found in language", HttpStatus.NOT_FOUND);

        String url = CommonUtils.makeUrl(byId.get().getTitle()) + "/" + CommonUtils.makeUrl(addSectionDTO.getTitle());
        if (repository.existsByUrl(url))
            throw RestException.restThrow(url + " url already exists", HttpStatus.BAD_REQUEST);

        Section section = new Section();
        section.setTitle(addSectionDTO.getTitle());
        section.setUrl(url);
        section.setLanguage(byId.get());
        section.setDescription(addSectionDTO.getDescription());
        section.setMaxRate(addSectionDTO.getMaxRate());
        repository.save(section);

        return ApiResult.successResponse(sectionToSectionDTO(section));
    }

    @Override
    public ApiResult<List<SectionDTO>> getSections() {
        List<SectionDTO> res = new ArrayList<>();

        for (Section section : repository.findAll()) {

            SectionDTO sectionDTO = sectionToSectionDTO(section);
            sectionDTO.setSolutionCount(userProblemRepository.countAllBySolvedAndProblem_Section_Id(true, section.getId()));
            sectionDTO.setProblemCount(userProblemRepository.countAllByProblem_Section_Id(section.getId()));
            sectionDTO.setTryCount(userProblemRepository.countAllByProblem_Section_Id(section.getId()));
        }
        return ApiResult.successResponse(res);
    }

    @Override
    public ApiResult<SectionDTO> getSection(Integer id) {
        Section section = repository.findById(id).orElseThrow(() ->
                RestException.restThrow("This id section not found", HttpStatus.NOT_FOUND));

        long problemCount = problemRepository.countProblemBySectionId(section.getId());
        long tryCount = userProblemRepository.countAllByProblem_SectionLanguageIdJPQL(section.getId());
        long solvedCount = userProblemRepository.countAllBySolvedIsTrueAndProblem_SectionLanguageId(section.getId());
        SectionDTO sectionDTO = sectionToSectionDTO(section);
        sectionDTO.setTryCount(tryCount);
        sectionDTO.setSolutionCount(solvedCount);
        sectionDTO.setProblemCount(problemCount);
        return ApiResult.successResponse(sectionDTO);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        repository.findById(id).orElseThrow(
                () -> RestException.restThrow("id not found", HttpStatus.BAD_REQUEST));
        languageRepository.deleteById(id);
        return ApiResult.successResponse("success deleted", true);
    }

    @Override
    public ApiResult<SectionDTO> edit(@NotNull(message = "section must be not null") SectionDTO sectionDTO,
                                      @NotNull(message = "section url path id must be not null") Integer id) {
        Optional<Section> lang = repository.getSectionByTitleAndLanguageId(sectionDTO.getTitle(),
                sectionDTO.getLanguage().getId());

        Section empty = repository.findById(id).orElseThrow(()->RestException.restThrow("section id not found",HttpStatus.NOT_FOUND));

        if (sectionDTO.getLanguage().getId() == null)
            throw RestException.restThrow("please enter language id", HttpStatus.BAD_REQUEST);

        Language language = languageRepository.findById(sectionDTO.getLanguage().getId()).orElseThrow(
                ()->RestException.restThrow("language not found",HttpStatus.NOT_FOUND));

        if (lang.isPresent() && !Objects.equals(id, lang.get().getId()))
            throw RestException.restThrow(sectionDTO.getTitle() + " already exists",HttpStatus.BAD_REQUEST);

        Section section = new Section();
        section.setId(id);
        section.setTitle(sectionDTO.getTitle() == null ?
                empty.getTitle() :
                sectionDTO.getTitle()
        );
        section.setUrl(sectionDTO.getUrl() == null ?
                CommonUtils.makeUrl(language.getTitle()) + "/" +
                        CommonUtils.makeUrl(sectionDTO.getTitle() == null
                                ? empty.getTitle()
                                : sectionDTO.getTitle())
                : sectionDTO.getUrl()
        );
        section.setDescription(sectionDTO.getDescription() == null ? empty.getDescription() : sectionDTO.getDescription());
        section.setLanguage(CommonUtils.languageDTOToLanguage(sectionDTO.getLanguage()));
        section.setMaxRate(sectionDTO.getMaxRate() == null ? empty.getMaxRate() : sectionDTO.getMaxRate());
        repository.save(section);
        return ApiResult.successResponse(sectionDTO);
    }

    private SectionDTO sectionToSectionDTO(Section section) {
        return new SectionDTO(
                section.getId(),
                section.getTitle(),
                section.getUrl(),
                section.getDescription(),
                section.getMaxRate(),
                CommonUtils.languageToLanguageDTO(section.getLanguage())
        );
    }


}
