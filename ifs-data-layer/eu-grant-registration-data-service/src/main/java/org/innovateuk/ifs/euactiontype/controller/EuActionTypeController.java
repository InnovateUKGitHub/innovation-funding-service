package org.innovateuk.ifs.euactiontype.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.euactiontype.transactional.EuActionTypeService;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for retrieving eu action types.
 */
@RestController
public class EuActionTypeController {

    private static final String baseURL = "/action-type";

    @Autowired
    private EuActionTypeService euActionTypeService;

    @GetMapping(baseURL + "/find-all")
    public RestResult<List<EuActionTypeResource>> findAll() {
        return euActionTypeService.findAll().toGetResponse();
    }

    @GetMapping(baseURL + "/get-by-id/{id}")
    public RestResult<EuActionTypeResource> getById(@PathVariable("id") long id) {
        return euActionTypeService.getById(id).toGetResponse();
    }
}
