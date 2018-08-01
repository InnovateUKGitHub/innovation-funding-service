package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling file types
 */
@RestController
@RequestMapping("/file/file-type")
public class FileTypeController {

    @Autowired
    private FileTypeService fileTypeService;

    @GetMapping("/{id}")
    public RestResult<FileTypeResource> findOne(@PathVariable("id") final long id) {
        return fileTypeService.findOne(id).toGetResponse();
    }

    @GetMapping("/findByName/{name}")
    public RestResult<FileTypeResource> findByName(@PathVariable("name") final String name) {
        return fileTypeService.findByName(name).toGetResponse();
    }
}
