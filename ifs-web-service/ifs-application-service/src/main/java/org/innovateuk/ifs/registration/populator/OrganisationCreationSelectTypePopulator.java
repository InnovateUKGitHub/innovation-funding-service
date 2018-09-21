package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Populator for Organisation creation lead applicant - choosing organisation type
 */
@Service
public class OrganisationCreationSelectTypePopulator {

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;
    
    public OrganisationCreationSelectTypeViewModel populate() {
        return
                new OrganisationCreationSelectTypeViewModel(
                        simpleFilter(
                                organisationTypeRestService.getAll().getSuccess(),
                                o -> OrganisationTypeEnum.getFromId(o.getId()) != null)
                );
    }
}
