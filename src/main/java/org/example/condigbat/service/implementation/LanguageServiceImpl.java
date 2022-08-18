package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.projection.LanguageDTOProjection;
import org.example.condigbat.repository.LanguageRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.LanguageService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    public static void main(String[] args) {

        String order = "worldabcefghijkmnpqstuvxyz";
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> res = new HashMap<>();


        String[] words = new String[]{"word","world","row"};

        for (int i = 0; i < order.length(); i++)
            map.put(String.valueOf(order.charAt(i)), order.indexOf(order.charAt(i)));

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                if (res.get(words[i].charAt(j)) == null) {
                    res.put(String.valueOf(words[i].charAt(j)), order.indexOf(words[i].charAt(j)));
                    System.out.println(map);
                }
                if (res.get(words[i].charAt(j)) > map.get(words[i].charAt(j))) {
                    System.out.println(false);
                }
            }
        }


        System.out.println(true);
    }

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

//        if (Objects.isNull(viewDTO) || (viewDTO.getFiltering().getColumns().isEmpty()
//                && viewDTO.getSearching().getValue().isBlank() && viewDTO.getSorting().isEmpty()))
//            stringBuilder.append(" GROUP BY l.id, l.title ORDER BY title");

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

        if (Objects.isNull(viewDTO))
            return;

        value.append(" WHERE ");
        if (!viewDTO.getSearching().getColumns().isEmpty()) {
            value.append("(");
            for (int i = 0; i < viewDTO.getSearching().getColumns().size(); i++) {
                value.append(" ");
                value.append(viewDTO.getSearching().getColumns().get(i));
                value.append(" ilike '%");
                value.append(viewDTO.getSearching().getValue());
                value.append("%'");
                value.append(i + 1 == viewDTO.getSearching().getColumns().size() ? "" : " OR ");
            }
            value.append(")");
            isPutConditionType = true;
        }
        if (!viewDTO.getFiltering().getColumns().isEmpty()) {
            if (isPutConditionType)
                value.append(" AND ");
            value.append("(");
            String con = viewDTO.getFiltering().getOperatorType().name();
            for (int i = 0; i < viewDTO.getFiltering().getColumns().size(); i++) {
                if (i != 0)
                    value.append(con);
                value.append(" ");
                value.append(viewDTO.getFiltering().getColumns().get(i).getTitle());
                value.append(" ");
                value.append(conditionType(viewDTO.getFiltering().getColumns().get(i).getConditionType().name(), viewDTO.getFiltering().getColumns().get(i)));
                value.append(" ");
            }
            value.append(")");
        }
        if (!viewDTO.getSorting().isEmpty()) {
            if (isPutConditionType)
                value.append(" AND ");
            value.append(" ORDER BY ");
            for (int i = 0; i < viewDTO.getSorting().size(); i++) {
                if (i != 0)
                    value.append(", ");
                value.append(viewDTO.getSorting().get(i).getName());
                value.append(" ");
                value.append(viewDTO.getSorting().get(i).getType());
            }
        } else {
            value.append(" ORDER BY title");
        }
    }

    private String conditionType(String str, FilterColumnDTO filterDTO) {
        switch (str) {
            case "EQ" -> {
                return "= " + filterDTO.getValue();
            }
            case "NOT_EQ" -> {
                return "<> " + filterDTO.getValue();
            }
            case "CONTAINS" -> {
                return "iLike '%" + filterDTO.getValue() + "%'";
            }
            case "NOT_CONTAINS" -> {
                return "not iLike '%" + filterDTO.getValue() + "%'";
            }
            case "GTE" -> {
                return ">= " + filterDTO.getValue();
            }
            case "GT" -> {
                return "> " + filterDTO.getValue();
            }
            case "LTE" -> {
                return "<= " + filterDTO.getValue();
            }
            case "LT" -> {
                return "< " + filterDTO.getValue();
            }
            case "RA" -> {
                return "between " + filterDTO.getFrom() + " and " + filterDTO.getTill();
            }
        }
        throw RestException.restThrow(str + " condition not found", HttpStatus.NOT_FOUND);
    }

}