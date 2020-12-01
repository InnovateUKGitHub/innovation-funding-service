package org.innovateuk.ifs.heukar.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.transactional.HeukarOrganisationTypeService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heukar-organisation-type")
public class HeukarOrganisationTypeController {

    @Autowired
    private HeukarOrganisationTypeService heukarOrganisationTypeService;

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<List<OrganisationTypeResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return heukarOrganisationTypeService.findByApplicationId(applicationId).toGetResponse();
    }

    @PostMapping("/add-new-org-type/{applicationId}/{organisationTypeId}")
    public RestResult<Void> addNewHeukarOrganisationType(@PathVariable("applicationId") final Long applicationId,
                                                         @PathVariable("organisationTypeId") final Long organisationTypeId) {
        return heukarOrganisationTypeService.addNewOrgTypeToApplication(applicationId, organisationTypeId)
                .toPostResponse();
    }

}
