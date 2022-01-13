package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fileentry")
public class FileEntryController {

    @Autowired
    private FileEntryService fileEntryService;

    @GetMapping("/{id}")
    public RestResult<FileEntryResource> findById(@PathVariable("id") final Long id) {
        return fileEntryService.findOne(id).toGetResponse();
    }
}
