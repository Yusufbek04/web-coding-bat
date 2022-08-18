package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.payload.enums.ConditionTypeEnum;
import org.example.condigbat.payload.enums.OperatorTypeEnum;
import org.example.condigbat.payload.enums.SortingTypeEnum;
import org.example.condigbat.repository.LanguageRepository;
import org.example.condigbat.repository.SectionRepository;
import org.example.condigbat.repository.UserProblemRepository;
import org.example.condigbat.service.serviceInt.LanguageService;
import org.example.condigbat.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    public static void main(String[] args) {
        Set<LanguageDTO> res = new HashSet<>();

        res.add(new LanguageDTO(1));
        LanguageDTO next = res.iterator().next();
        System.out.println(next);
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
    public ApiResult<List<LanguageDTO>> getLanguages(ViewDTO viewDTO) {
        List<LanguageDTO> res = new ArrayList<>();

        for (Language language : languageRepository.findAll()) {
            res.add(mapLanguageToLanguageDTO(language,
                    sectionRepository.countAllByLanguage_Id(language.getId()),
                    userProblemRepository.countAllByProblem_SectionLanguageId(language.getId()),
                    userProblemRepository.
                            countAllBySolvedIsTrueAndProblem_SectionLanguageId(language.getId())));

        }
        if (viewDTO.getSearching() != null)
            res = search(viewDTO.getSearching(), res);

        if (viewDTO.getFiltering() != null && viewDTO.getFiltering().getOperatorType() == OperatorTypeEnum.AND)
            res = filterAnd(viewDTO.getFiltering(), res);
        else if (viewDTO.getFiltering() != null)
            res = filterOr(viewDTO.getFiltering(), res);
        if (viewDTO.getSorting() != null)
            res = sort(viewDTO.getSorting(), res);

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

    private List<LanguageDTO> search(SearchingDTO searchingDTO, List<LanguageDTO> list) {

        if (searchingDTO.getColumns().size() == 2) {
            list = list.stream().filter(x -> x.getTitle().contains(searchingDTO.getValue()) ||
                    x.getUrl().contains(searchingDTO.getValue())).collect(Collectors.toList());
        } else if (searchingDTO.getColumns().size() == 1) {
            if (searchingDTO.getColumns().get(0).equals("title")) {
                list = list.stream().filter(x -> x.getTitle().contains(searchingDTO.getValue())).collect(Collectors.toList());
            } else if (searchingDTO.getColumns().get(0).equals("url")) {
                list = list.stream().filter(x -> x.getUrl().contains(searchingDTO.getValue())).collect(Collectors.toList());
            } else
                throw RestException.restThrow("select only title or url", HttpStatus.NOT_FOUND);
        }
        return list;
    }

    private List<LanguageDTO> sort(List<SortingDTO> sortingDTOS, List<LanguageDTO> languageDTOS) {

        Collections.sort(languageDTOS, new Comparator<LanguageDTO>() {
            @Override
            public int compare(LanguageDTO o1, LanguageDTO o2) {
                if (sortingDTOS == null || sortingDTOS.size() == 0)
                    return 1;
                int num;
                for (SortingDTO sortingDTO : sortingDTOS) {
                    int asc = sortingDTO.getType() == SortingTypeEnum.ASC ? 1 : -1;
                    switch (sortingDTO.getName()) {
                        case "id" -> {
                            num = o1.getId().compareTo(o2.getId());
                            if (num != 0)
                                return asc * num;
                        }
                        case "title" -> {
                            num = o1.getTitle().compareTo(o2.getTitle());
                            if (num != 0)
                                return asc * num;
                        }
                        case "url" -> {
                            num = o1.getUrl().compareTo(o2.getUrl());
                            if (num != 0)
                                return asc * num;
                        }
                        case "sectionCount" -> {
                            num = o1.getSectionCount().compareTo(o2.getSectionCount());
                            if (num != 0)
                                return asc * num;
                        }
                        case "tryCount" -> {
                            num = o1.getTryCount().compareTo(o2.getTryCount());
                            if (num != 0)
                                return asc * num;
                        }
                        case "solutionCount" -> {
                            num = o1.getSolutionCount().compareTo(o2.getSolutionCount());
                            if (num != 0)
                                return asc * num;
                        }
                        default -> throw new IllegalArgumentException();
                    }
                }
                return 1;
            }
        });

        return languageDTOS;
    }


    private List<LanguageDTO> filterAnd(FilterDTO filterDTO, List<LanguageDTO> a) {
        Set<LanguageDTO> languagesDTO = new HashSet<>(a);

        for (FilterColumnDTO column : filterDTO.getColumns()) {
            switch (column.getTitle()) {
                case "title":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x ->
                                x.getTitle().contains(column.getValue())
                        ).collect(Collectors.toSet());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x ->
                                !x.getTitle().contains(column.getValue())
                        ).collect(Collectors.toSet());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "url":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x -> x.getUrl().contains(column.getValue())).collect(Collectors.toSet());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getUrl().contains(column.getValue())).collect(Collectors.toSet());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "tryCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().
                                filter(x -> x.getTryCount().equals(Long.valueOf(column.getValue()))).
                                collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getTryCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() >= Long.parseLong(column.getFrom()) &&
                                x.getTryCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toSet());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "solutionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getSolutionCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSolutionCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toSet());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "sectionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount().equals(Integer.valueOf(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getSectionCount().equals(Integer.valueOf(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toSet());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSectionCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toSet());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                default:
                    throw RestException.restThrow("column not found", HttpStatus.NOT_FOUND);
            }
        }
        return languagesDTO.stream().toList();
    }

    private List<LanguageDTO> filterOr(FilterDTO filterDTO, List<LanguageDTO> a) {
        Set<LanguageDTO> languagesDTO = new HashSet<>(a);
        Set<LanguageDTO> res = new HashSet<>();

        for (FilterColumnDTO column : filterDTO.getColumns()) {
            switch (column.getTitle()) {
                case "title":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                        res.add(languagesDTO.stream().filter(x ->
                                x.getTitle().contains(column.getValue())
                        ).iterator().next());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        res.add(languagesDTO.stream().filter(x ->
                                !x.getTitle().contains(column.getValue())
                        ).iterator().next());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "url":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                        res.add(languagesDTO.stream().filter(x -> x.getUrl().contains(column.getValue())).iterator().next());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        res.add(languagesDTO.stream().filter(x -> !x.getUrl().contains(column.getValue())).iterator().next());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "tryCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        res.add(languagesDTO.stream().
                                filter(x -> x.getTryCount().equals(Long.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        res.add(languagesDTO.stream().filter(x -> !x.getTryCount().equals(Long.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        res.add(languagesDTO.stream().filter(x -> x.getTryCount() > (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        res.add(languagesDTO.stream().filter(x -> x.getTryCount() >= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        res.add(languagesDTO.stream().filter(x -> x.getTryCount() < (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        res.add(languagesDTO.stream().filter(x -> x.getTryCount() <= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        res.add(languagesDTO.stream().filter(x -> x.getTryCount() >= Long.parseLong(column.getFrom()) &&
                                x.getTryCount() <= Long.parseLong(column.getFrom())).iterator().next());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "solutionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount().equals(Long.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        res.add(languagesDTO.stream().filter(x -> !x.getSolutionCount().equals(Long.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount() > (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount() >= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount() < (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount() <= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        res.add(languagesDTO.stream().filter(x -> x.getSolutionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSolutionCount() <= Long.parseLong(column.getFrom())).iterator().next());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "sectionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount().equals(Integer.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        res.add(languagesDTO.stream().filter(x -> !x.getSectionCount().equals(Integer.valueOf(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount() > (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount() >= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount() < (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount() <= (Long.parseLong(column.getValue()))).iterator().next());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        res.add(languagesDTO.stream().filter(x -> x.getSectionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSectionCount() <= Long.parseLong(column.getFrom())).iterator().next());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                default:
                    throw RestException.restThrow("column not found", HttpStatus.NOT_FOUND);
            }
        }
        return res.stream().toList();
    }

}