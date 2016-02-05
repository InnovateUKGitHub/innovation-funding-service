package com.worth.ifs.file.controller;

import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fileentry")
public class FileEntryController {
    @Autowired
    private FileEntryService service;

    @Autowired
    private FileEntryMapper mapper;

    @RequestMapping("/{id}")
    public FileEntryResource findById(@PathVariable("id") final Long id) {
        return mapper.mapFileEntryToResource(service.findOne(id));
    }
}