package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddLanguageDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.LanguageDTO;
import org.example.condigbat.payload.ViewDTO;
import org.example.condigbat.projection.LanguageDTOProjection;
import org.example.condigbat.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RequestMapping(path = "/language")
public interface LanguageController {

    @PostMapping(path = "/add")
    ApiResult<LanguageDTO> add(@Valid @RequestBody AddLanguageDTO addLanguageDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @PostMapping("/list")
    ApiResult<List<LanguageDTOProjection>> getLanguages(@RequestBody(required = false) ViewDTO viewDTO,
                                                        @RequestParam(defaultValue = RestConstants.DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(defaultValue = RestConstants.DEFAULT_PAGE_SIZE) int size);

    @GetMapping("/{id}")
    ApiResult<LanguageDTO> getLanguage(@PathVariable @Valid @NotNull(message = "Id must not be null") Integer id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Integer id);

    @PutMapping("/{id}")
    ApiResult<LanguageDTO> edit(@RequestBody LanguageDTO languageDTO,
                                @PathVariable Integer id);
}
