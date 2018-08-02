package org.innovateuk.ifs.profile.populator;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.profile.viewmodel.OrganisationProfileViewModel;
import org.innovateuk.ifs.profile.viewmodel.UserProfileViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

@Component
public class UserProfilePopulator {

    @Autowired
    private OrganisationRestService organisationRestService;


    public UserProfileViewModel populate(UserResource user) {
        List<OrganisationResource> organisations = organisationRestService.getAllByUserId(user.getId()).getSuccess();
        Set<OrganisationProfileViewModel> organisationViewModels = simpleMapSet(organisations, this::toOrganisationViewModel);
        String name;
        if (user.getTitle() != null) {
            name = user.getTitle() + " " + user.getName().trim();
        } else {
            name = user.getName();
        }
        return new UserProfileViewModel(name, user.getPhoneNumber(), user.getEmail(), user.getAllowMarketingEmails(), organisationViewModels);
    }

    private OrganisationProfileViewModel toOrganisationViewModel(OrganisationResource organisation) {
        return new OrganisationProfileViewModel(organisation.getName(), organisation.getCompanyHouseNumber(), organisation.getOrganisationTypeName());
    }
}
