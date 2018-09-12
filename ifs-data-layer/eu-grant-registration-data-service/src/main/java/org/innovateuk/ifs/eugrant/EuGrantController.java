package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for saving and getting eu grant registrations.
 */
@RestController
public class EuGrantController {

    @Autowired
    private EuGrantService euGrantService;

    @PostMapping("/eu-grant")
    public RestResult<EuGrantResource> create() {
        return euGrantService.create().toPostCreateResponse();
    }

    @PutMapping("/eu-grant/{uuid}")
    public RestResult<Void> update(@PathVariable("uuid") UUID uuid,
                                              @RequestBody EuGrantResource euGrant) {
        return euGrantService.save(euGrant).toPutResponse();
    }

    @GetMapping("/eu-grant/{uuid}")
    public RestResult<EuGrantResource> getEuGrant(@PathVariable("uuid") UUID uuid) {
        return euGrantService.findById(uuid).toGetResponse();
    }

}
