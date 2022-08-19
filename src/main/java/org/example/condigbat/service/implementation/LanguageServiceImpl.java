package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.payload.enums.ConditionTypeEnum;
import org.example.condigbat.projection.LanguageDTOProjection;
import org.example.condigbat.repository.LanguageRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.LanguageService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public ApiResult<List<LanguageDTOProjection>> getLanguages(ViewDTO viewDTO, int page, int size) {
        StringBuilder stringBuilder = new StringBuilder(
                "with temp as(" +
                        "          SELECT l.*," +
                        "          COUNT(s.id)                               AS section_count," +
                        "          COUNT(p.id)                               AS problem_count," +
                        "          COUNT(up.id)                              AS try_count," +
                        "          COUNT(CASE WHEN up.solved THEN up.id END) AS solution_count" +
                        "                         FROM language l" +
                        "                                 LEFT JOIN section s on l.id = s.language_id" +
                        "                                 LEFT JOIN problem p on s.id = p.section_id" +
                        "                                 LEFT JOIN user_problem up on p.id = up.problem_id" +
                        "" +
                        " GROUP BY l.id)" +
                        "SELECT * FROM temp"
        );

        filtering(viewDTO, stringBuilder);
        stringBuilder
                .append(" LIMIT ")
                .append(size)
                .append(" OFFSET ")
                .append(page * size);
        String query = stringBuilder.toString();
        System.out.println(query);
        List<LanguageDTOProjection> languagesByStringQuery = languageRepository.getLanguagesByStringQuery(query);
        return ApiResult.successResponse(languagesByStringQuery);
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


    /**
     * Hello World
     */

    private LanguageDTO mapLanguageToLanguageDTO(Language language, int sectionCount, long tryCount, long solvedCount) {
        return new LanguageDTO(
                language.getId(),
                language.getTitle(),
                language.getUrl(),
                sectionCount,
                tryCount,
                solvedCount);
    }

    private void filtering(ViewDTO viewDTO, StringBuilder value) {
        boolean isPutConditionType = false;
        boolean needWhere = false;
        if (Objects.isNull(viewDTO))
            return;
        value.append(" WHERE ");
        if (!viewDTO.getSearching().getColumns().isEmpty()) {
            needWhere = true;
            value.append("(");
            for (int i = 0; i < viewDTO.getSearching().getColumns().size(); i++) {
                value.append(" ")
                        .append(viewDTO.getSearching().getColumns().get(i).replaceAll("Count", "_count"))
                        .append(" iLike '%")
                        .append(viewDTO.getSearching().getValue())
                        .append("%'")
                        .append(i + 1 == viewDTO.getSearching().getColumns().size() ? "" : " OR ");
            }
            value.append(")");
            isPutConditionType = true;
        }
        if (!viewDTO.getFiltering().getColumns().isEmpty()) {
            needWhere = true;
            if (isPutConditionType)
                value.append(" AND ");
            value.append("(");
            String con = viewDTO.getFiltering().getOperatorType().name();
            for (int i = 0; i < viewDTO.getFiltering().getColumns().size(); i++) {
                if (i != 0)
                    value.append(con);
                value.append(" ")
                        .append(viewDTO.getFiltering().getColumns().get(i).getName().replaceAll("Count", "_count"))
                        .append(" ")
                        .append(conditionType(viewDTO.getFiltering().getColumns().get(i).getConditionType(), viewDTO.getFiltering().getColumns().get(i)))
                        .append(" ");
            }
            value.append(")");
        }
        if (!viewDTO.getSorting().isEmpty()) {

            value.append(" ORDER BY ");
            for (int i = 0; i < viewDTO.getSorting().size(); i++) {
                if (i != 0)
                    value.append(", ");
                value.append(viewDTO.getSorting().get(i).getName().replaceAll("Count", "_count"))
                        .append(" ")
                        .append(viewDTO.getSorting().get(i).getType());
            }
        } else
            value.append(" ORDER BY title");
        if (!needWhere) {
            int index = value.indexOf("WHERE");
            value.replace(index, index + 5, "");
        }
    }

    private String conditionType(ConditionTypeEnum con, FilterColumnDTO filterDTO) {
        return switch (con) {
            case EQ -> "= " + filterDTO.getValue();
            case NOT_EQ -> "<> " + filterDTO.getValue();
            case CONTAINS -> "iLike '%" + filterDTO.getValue() + "%'";
            case NOT_CONTAINS -> "not iLike '%" + filterDTO.getValue() + "%'";
            case GTE -> ">= " + filterDTO.getValue();
            case GT -> "> " + filterDTO.getValue();
            case LTE -> "<= " + filterDTO.getValue();
            case LT -> "< " + filterDTO.getValue();
            case RA -> "between " + filterDTO.getFrom() + " and " + filterDTO.getTill();
        };
    }
}