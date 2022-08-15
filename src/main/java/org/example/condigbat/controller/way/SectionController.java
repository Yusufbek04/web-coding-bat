package org.example.condigbat.controller.way;

import org.example.condigbat.payload.AddSectionDTO;
import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.SectionDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping(path = "/section")
public interface SectionController {

    @PostMapping(path = "/add")
    ApiResult<SectionDTO> add(@RequestBody AddSectionDTO AddSectionDTO);

    //TODO buni POST qilamiz va @RequestBody dan filter, search, sort qilamiz
    @GetMapping("/list")
    ApiResult<List<SectionDTO>> getSections();

    @GetMapping("/{id}")
    ApiResult<SectionDTO> getSection(@PathVariable Integer id);

    @DeleteMapping("/{id}")
    ApiResult<Boolean> delete(@PathVariable Integer id);

    @PutMapping("/{id}")
    ApiResult<SectionDTO> edit(@RequestBody SectionDTO sectionDTO,
                                @PathVariable Integer id);
}
