package org.example.condigbat.controller;

import lombok.RequiredArgsConstructor;
import org.example.condigbat.controller.way.SectionController;
import org.example.condigbat.payload.AddSectionDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.SectionDTO;
import org.example.condigbat.service.serviceInt.SectionService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SectionControllerImpl implements SectionController {

    private final SectionService sectionsService;

    @Override
    public ApiResult<SectionDTO> add(AddSectionDTO addSectionDTO) {

        return sectionsService.add(addSectionDTO);
    }

    @Override
    public ApiResult<List<SectionDTO>> getSections() {
        return sectionsService.getSections();
    }

    @Override
    public ApiResult<SectionDTO> getSection(Integer id) {
        return sectionsService.getSection(id);
    }

    @Override
    public ApiResult<Boolean> delete(Integer id) {
        return sectionsService.delete(id);
    }

    @Override
    public ApiResult<SectionDTO> edit(SectionDTO sectionDTO, Integer id) {
        return sectionsService.edit(sectionDTO,id);
    }
}
