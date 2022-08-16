package org.example.condigbat.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.entity.Language;
import org.example.condigbat.error.RestException;
import org.example.condigbat.payload.*;
import org.example.condigbat.payload.enums.ConditionTypeEnum;
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
        List<String> res = new ArrayList<>(List.of("Java", "C++", "Php", "Python", "JavaScript"));
        Collections.sort(res);
        System.out.println(res);
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
        if (viewDTO.getFiltering() != null)
            res = filter(viewDTO.getFiltering(), res);
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

    private List<LanguageDTO> filter(FilterDTO filterDTO, List<LanguageDTO> languagesDTO) {

        for (FilterColumnDTO column : filterDTO.getColumns()) {
            switch (column.getTitle()) {
                case "title":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                            languagesDTO = languagesDTO.stream().filter(x ->
                                    x.getTitle().contains(column.getValue())
                            ).collect(Collectors.toList());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x ->
                                !x.getTitle().contains(column.getValue())
                        ).collect(Collectors.toList());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "url":
                    if (column.getConditionType() == ConditionTypeEnum.CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x -> x.getUrl().contains(column.getValue())).collect(Collectors.toList());
                    } else if (column.getConditionType() == ConditionTypeEnum.NOT_CONTAINS) {
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getUrl().contains(column.getValue())).collect(Collectors.toList());
                    } else
                        throw RestException.restThrow(column.getConditionType() +
                                " condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "tryCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getTryCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getTryCount() >= Long.parseLong(column.getFrom()) &&
                                x.getTryCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toList());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "solutionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getSolutionCount().equals(Long.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSolutionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSolutionCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toList());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                case "sectionCount":
                    if (column.getConditionType() == ConditionTypeEnum.EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount().equals(Integer.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.NOT_EQ)
                        languagesDTO = languagesDTO.stream().filter(x -> !x.getSectionCount().equals(Integer.valueOf(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() > (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.GTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() >= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LT)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() < (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.LTE)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() <= (Long.parseLong(column.getValue()))).collect(Collectors.toList());
                    else if (column.getConditionType() == ConditionTypeEnum.RA)
                        languagesDTO = languagesDTO.stream().filter(x -> x.getSectionCount() >= Long.parseLong(column.getFrom()) &&
                                x.getSectionCount() <= Long.parseLong(column.getFrom())).collect(Collectors.toList());
                    else
                        throw RestException.restThrow("condition not found", HttpStatus.NOT_FOUND);
                    break;
                default:
                    throw RestException.restThrow("column not found", HttpStatus.NOT_FOUND);
            }
        }
        return languagesDTO;
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

        List<LanguageDTO> test = new ArrayList<>(languageDTOS);

        List<LanguageDTO> res = new ArrayList<>();
        int num;
        for (int loop = 0; loop < languageDTOS.size(); loop++) {
            for (int i = 0; i < languageDTOS.size(); i++) {
                if (i + 1 == languageDTOS.size()) {
                    res.add(languageDTOS.get(i).getId().equals(res.get(i - 1).getId()) ? languageDTOS.get(i - 1) : languageDTOS.get(i));
                    System.out.println(res);
                    System.out.println(languageDTOS);
                    break;
                }
                for (SortingDTO sortingDTO : sortingDTOS) {
                    num = sorting(sortingDTO.getName(), languageDTOS.get(i), languageDTOS.get(i + 1));
                    if (num > 0) {
                        res.add(sortingDTO.getType().name().equals("ASC")
                                ? test.remove(0)
                                : test.remove(1));
                        System.out.println(res);
                        break;
                    } else if (num < 0) {
                        res.add(sortingDTO.getType().name().equals("ASC")
                                ? test.remove(1)
                                : test.remove(0));
                        System.out.println(res);
                        break;
                    }
                }
            }
            test = new ArrayList<>(res);
            res = new ArrayList<>();
        }
        return test;
    }


    private int sorting(String name, LanguageDTO compare, LanguageDTO to) {
        return switch (name) {
            case "title" -> compare.getTitle().compareTo(to.getTitle());
            case "url" -> compare.getUrl().compareTo(to.getUrl());
            case "tryCount" -> compare.getTryCount().compareTo(to.getTryCount());
            case "solutionCount" -> compare.getSolutionCount().compareTo(to.getSolutionCount());
            case "sectionCount" -> compare.getSectionCount().compareTo(to.getSectionCount());
            default -> throw RestException.restThrow(name + " column not found", HttpStatus.NOT_FOUND);
        };
    }
}