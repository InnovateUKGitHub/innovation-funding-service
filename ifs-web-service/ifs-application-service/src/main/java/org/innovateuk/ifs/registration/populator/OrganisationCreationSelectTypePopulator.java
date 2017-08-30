package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Populator for Organisation creation lead applicant - choosing organisation type
 */
@Service
public class OrganisationCreationSelectTypePopulator {

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    public OrganisationCreationSelectTypeViewModel populate() {
        List<OrganisationTypeResource> orgTypes = organisationTypeRestService.getAll().getSuccessObject()
                .stream()
                .filter(organisationTypeResource -> OrganisationTypeEnum.getFromId(organisationTypeResource.getId()) != null)
                .collect(Collectors.toList());

        return new OrganisationCreationSelectTypeViewModel(orgTypes);
    }
}
