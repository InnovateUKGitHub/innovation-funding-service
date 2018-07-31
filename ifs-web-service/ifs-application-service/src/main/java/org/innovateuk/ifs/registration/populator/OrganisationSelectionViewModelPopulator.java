package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.OrganisationSelectionChoiceViewModel;
import org.innovateuk.ifs.registration.viewmodel.OrganisationSelectionViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class OrganisationSelectionViewModelPopulator {

    private OrganisationRestService organisationRestService;

    private RegistrationCookieService registrationCookieService;

    @Autowired
    public OrganisationSelectionViewModelPopulator(OrganisationRestService organisationRestService, RegistrationCookieService registrationCookieService) {
        this.organisationRestService = organisationRestService;
        this.registrationCookieService = registrationCookieService;
    }

    public OrganisationSelectionViewModel populate(UserResource userResource, HttpServletRequest request, String newOrganisationUrl) {
        Set<OrganisationSelectionChoiceViewModel> choices = organisationRestService.getAllUsersOrganisations(userResource.getId()).getSuccess()
                .stream()
                .map(this::choice)
                .collect(toSet());
        return new OrganisationSelectionViewModel(choices, registrationCookieService.isCollaboratorJourney(request), newOrganisationUrl);
    }

    private OrganisationSelectionChoiceViewModel choice(OrganisationResource organisation) {
        return new OrganisationSelectionChoiceViewModel(organisation.getId(),
                    organisation.getName(),
                    organisation.getOrganisationTypeName());
    }
}
