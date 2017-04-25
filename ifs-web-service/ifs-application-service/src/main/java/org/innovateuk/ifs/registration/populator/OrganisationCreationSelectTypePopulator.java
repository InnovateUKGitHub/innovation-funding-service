package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Populator for Organisation creation lead applicant - choosing organisation type
 */
@Service
public class OrganisationCreationSelectTypePopulator {

    private static final List<Long> ALLOWED_LEAD_ORG_TYPES = asList(OrganisationTypeEnum.BUSINESS.getId(), OrganisationTypeEnum.RTO.getId());

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    public OrganisationCreationSelectTypeViewModel populate() {
        List<OrganisationTypeResource> orgTypes = organisationTypeRestService.getAll().getSuccessObject()
                .stream()
                .filter(organisationTypeResource -> ALLOWED_LEAD_ORG_TYPES.contains(organisationTypeResource.getId()))
                .collect(Collectors.toList());

        return new OrganisationCreationSelectTypeViewModel(orgTypes);
    }
}
