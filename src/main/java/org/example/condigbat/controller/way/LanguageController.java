package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddLanguageDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.LanguageDTO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RequestMapping(path = "/language")
public interface LanguageController {

    @PostMapping(path = "/add")
    ApiResult<LanguageDTO> add(@Valid @RequestBody AddLanguageDTO addLanguageDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @GetMapping("/list")
    ApiResult<List<LanguageDTO>> getLanguages();

    @GetMapping("/{id}")
    ApiResult<LanguageDTO> getLanguage(@PathVariable @NotNull(message = "Id must not be null") Integer id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Integer id);

    @PutMapping("/{id}")
    ApiResult<LanguageDTO> edit(@RequestBody LanguageDTO languageDTO,
                                @PathVariable Integer id);
}
