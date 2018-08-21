package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class EuGrantController {

    @Autowired
    private EuGrantService euGrantService;

    @PostMapping("/eu-grant")
    public RestResult<EuGrantResource> saveEuGrant(@RequestBody EuGrantResource euGrant) {
        return euGrantService.save(euGrant).toPostCreateResponse();
    }

    @GetMapping("/eu-grant/{uuid}")
    public RestResult<EuGrantResource> getEuGrant(@PathVariable("uuid") UUID uuid) {
        return euGrantService.findById(uuid).toGetResponse();
    }

}
