package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.AddLanguageDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.LanguageDTO;
import org.example.condigbat.repository.LanguageRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.LanguageService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    private final SectionRepository sectionRepository;

    private final UserProblemRepository userProblemRepository;

    @Override
    public ApiResult<LanguageDTO> add(AddLanguageDTO addLanguageDTO) {

        if (languageRepository.existsByTitle(addLanguageDTO.getTitle()))
            throw RestException.restThrow("This language already exists", HttpStatus.CONFLICT);

        Language language = new Language();
        language.setTitle(addLanguageDTO.getTitle());

        languageRepository.save(language);

        LanguageDTO languageDTO = mapLanguageToLanguageDTO(language,
                0,
                0L,
                0L);

        return ApiResult.successResponse("Successfully saved", languageDTO);
    }

    @Override
    public ApiResult<List<LanguageDTO>> getLanguages() {
        List<LanguageDTO> res = new ArrayList<>();

        for (Language language : languageRepository.findAll())
            res.add(mapLanguageToLanguageDTO(language,
                    sectionRepository.countAllByLanguage_Id(language.getId()),
                    userProblemRepository.countAllByProblem_SectionLanguageId(language.getId()),
                    userProblemRepository.
                            countAllBySolvedIsTrueAndProblem_SectionLanguageId(language.getId())));
        return ApiResult.successResponse(res);
    }

    @Override
    public ApiResult<LanguageDTO> getLanguage(Integer id) {
        Language language = languageRepository.findById(id).orElseThrow(() ->
                RestException.restThrow("This id language not found", HttpStatus.NOT_FOUND));

        long sectionCount = sectionRepository.countAllByLanguageId(language.getId());
        long tryCount = userProblemRepository.countAllByProblem_SectionLanguageIdJPQL(language.getId());
        long solvedCount = userProblemRepository.countAllBySolvedIsTrueAndProblem_SectionLanguageId(language.getId());
        LanguageDTO languageDTO = mapLanguageToLanguageDTO(language,
                Long.valueOf(sectionCount).intValue(),
                tryCount,
                solvedCount);

        return ApiResult.successResponse(languageDTO);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        languageRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("id not found", HttpStatus.BAD_REQUEST)
        );
        languageRepository.deleteById(id);
        return ApiResult.successResponse("success deleted", true);
    }

    @Override
    public ApiResult<LanguageDTO> edit(LanguageDTO languageDTO, Integer id) {

        Optional<Language> lang = languageRepository.getLanguageByTitle(languageDTO.getTitle());

        if (languageRepository.findById(id).isEmpty())
            throw RestException.restThrow("language id not found", HttpStatus.NOT_FOUND);

        if (lang.isPresent() && !Objects.equals(id, lang.get().getId()))
            throw RestException.restThrow(languageDTO.getTitle() + " already exists", HttpStatus.BAD_REQUEST);

        Language language = new Language();
        language.setId(id);
        language.setTitle(languageDTO.getTitle());
        language.setUrl(CommonUtils.makeUrl(languageDTO.getTitle()));
        languageRepository.save(language);
        languageDTO.setUrl(CommonUtils.makeUrl(languageDTO.getTitle()));
        return ApiResult.successResponse(languageDTO);
    }

    private LanguageDTO mapLanguageToLanguageDTO(Language language,
                                                 int sectionCount,
                                                 long tryCount,
                                                 long solvedCount) {
        return new LanguageDTO(
                language.getId(),
                language.getTitle(),
                language.getUrl(),
                sectionCount,
                tryCount,
                solvedCount);
    }

}
