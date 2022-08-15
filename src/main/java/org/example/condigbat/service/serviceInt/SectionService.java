package org.example.condigbat.service.serviceInt;

import org.example.condigbat.payload.AddSectionDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.SectionDTO;

import java.util.List;

public interface SectionService {
    ApiResult<SectionDTO> add(AddSectionDTO sectionDTO);

    ApiResult<List<SectionDTO>> getSections();

    ApiResult<SectionDTO> getSection(Integer id);

    ApiResult<Boolean> delete(Integer id);

    ApiResult<SectionDTO> edit(SectionDTO sectionDTO, Integer id);
}
