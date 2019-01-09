package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.isResearch;

@Component
public class ConfirmOrganisationInviteModelPopulator {

    public ConfirmOrganisationInviteOrganisationViewModel populate(ApplicationInviteResource invite,
                                                                   OrganisationResource organisation,
                                                                   String registerUrl) {
        String partOfOrganisation = invite.getInviteOrganisationNameConfirmedSafe();
        String organisationType = organisation.getOrganisationTypeName();
        String registrationName = organisation.getName();
        String registrationNumber = organisation.getCompaniesHouseNumber();
        String leadApplicantEmail = invite.getLeadApplicantEmail();
        boolean showRegistrationNumber =
                !(isEmpty(registrationNumber) || isResearch(organisation.getOrganisationType()));
        boolean leadOrganisation = invite.getLeadOrganisationId().equals(organisation.getId());

        return new ConfirmOrganisationInviteOrganisationViewModel(partOfOrganisation, organisationType,
                registrationName, registrationNumber, leadApplicantEmail, showRegistrationNumber, leadOrganisation,
                registerUrl);
    }
}
