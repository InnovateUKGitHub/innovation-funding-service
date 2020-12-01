package org.innovateuk.ifs.heukar.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.transactional.HeukarPartnerOrganisationService;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heukar-partner-organisation")
public class HeukarPartnerOrganisationController {

    @Autowired
    private HeukarPartnerOrganisationService heukarPartnerOrganisationService;

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return heukarPartnerOrganisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @PostMapping("/add-new-org-type/{applicationId}/{organisationTypeId}")
    public RestResult<Void> addNewHeukarPartnerOrganisation(@PathVariable("applicationId") final Long applicationId,
                                                            @PathVariable("organisationTypeId") final Long organisationTypeId) {
        return heukarPartnerOrganisationService.addNewPartnerOrgToApplication(applicationId, organisationTypeId)
                .toPostResponse();
    }

    @PutMapping("/{id}")
    public RestResult<Void> updateHeukarPartnerOrganisation(@PathVariable("id") Long id) {
        return heukarPartnerOrganisationService.updatePartnerOrganisation(id).toPutResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> deleteHeukarOrganisationType(@PathVariable("id") Long id) {
        return heukarPartnerOrganisationService.deletePartnerOrganisation(id).toDeleteResponse();
    }

    @GetMapping("/{id}")
    public RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(@PathVariable("id") Long id) {
        return heukarPartnerOrganisationService.findOne(id).toGetResponse();
    }

}
