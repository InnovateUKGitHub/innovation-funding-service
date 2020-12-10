package org.innovateuk.ifs.heukar.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;
import org.innovateuk.ifs.heukar.transactional.HeukarPartnerOrganisationService;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecuredBySpring(value = "Controller", description = "Only applicants can add/remove/update Heukar partner organisation",
        securedType = HeukarPartnerOrganisationController.class)
@PreAuthorize("hasAuthority('applicant')")
@RequestMapping("/heukar-partner-organisation")
public class HeukarPartnerOrganisationController {

    @Autowired
    private HeukarPartnerOrganisationService heukarPartnerOrganisationService;

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(@PathVariable final long applicationId) {
        return heukarPartnerOrganisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @PostMapping("/add-new-org-type/{applicationId}/{organisationTypeId}")
    public RestResult<Void> addNewHeukarPartnerOrganisation(@PathVariable final long applicationId,
                                                            @PathVariable final long organisationTypeId) {
        return heukarPartnerOrganisationService.addNewPartnerOrgToApplication(applicationId, organisationTypeId)
                .toPostResponse();
    }

    @PutMapping("/{id}/{organisationTypeId}")
    public RestResult<Void> updateHeukarPartnerOrganisation(@PathVariable long id,
                                                            @PathVariable long organisationTypeId) {
        return heukarPartnerOrganisationService.updatePartnerOrganisation(id, organisationTypeId).toPutResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> deleteHeukarOrganisationType(@PathVariable long id) {
        return heukarPartnerOrganisationService.deletePartnerOrganisation(id).toDeleteResponse();
    }

    @GetMapping("/{id}")
    public RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(@PathVariable long id) {
        return heukarPartnerOrganisationService.findOne(id).toGetResponse();
    }

    @GetMapping("/all-org-types")
    public RestResult<List<HeukarPartnerOrganisationTypeEnum>> getAllHeukarPartnerOrganisationTypes(){
        return heukarPartnerOrganisationService.getAllHeukarPartnerOrganisationTypes().toGetResponse();
    }

}
