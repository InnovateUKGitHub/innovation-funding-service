package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.transactional.EuActionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for retrieving eu action types.
 */
@RestController
public class EuActionTypeController {

    private static final String baseURL = "/eu-grant/action-type";

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
