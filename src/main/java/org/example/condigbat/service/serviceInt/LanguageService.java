package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.AddLanguageDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.LanguageDTO;
import org.example.condigbat.payload.ViewDTO;

import java.util.List;

public interface LanguageService {
    ApiResult<LanguageDTO> add(AddLanguageDTO addLanguageDTO);

    ApiResult<List<LanguageDTO>> getLanguages(ViewDTO viewDTO);

    ApiResult<LanguageDTO> getLanguage(Integer id);

    ApiResult<Boolean> delete(Integer id);

    ApiResult<LanguageDTO> edit(LanguageDTO languageDTO, Integer id);
}

