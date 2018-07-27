package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.profile.populator.AssessorProfileDeclarationBasePopulator;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AssessorProfileDeclarationModelPopulator extends AssessorProfileDeclarationBasePopulator {

    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;
    private AffiliationRestService affiliationRestService;

    public AssessorProfileDeclarationModelPopulator(AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator,
                                                    AffiliationRestService affiliationRestService) {
        this.assessorProfileDetailsModelPopulator = assessorProfileDetailsModelPopulator;
        this.affiliationRestService = affiliationRestService;
    }

    public AssessorProfileDeclarationViewModel populateModel(UserResource user) {

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = assessorProfileDetailsModelPopulator.populateModel(user);

        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(affiliationRestService.getUserAffiliations(user.getId()).getSuccess().getAffiliationResourceList());

        Optional<AffiliationResource> principalEmployer = getPrincipalEmployer(affiliations);

        return new AssessorProfileDeclarationViewModel(
                assessorProfileDetailsViewModel,
                affiliations.size() > 0,
                principalEmployer.map(AffiliationResource::getOrganisation).orElse(null),
                principalEmployer.map(AffiliationResource::getPosition).orElse(null),
                getProfessionalAffiliations(affiliations),
                getAppointments(affiliations),
                getFinancialInterests(affiliations),
                getFamilyAffiliations(affiliations),
                getFamilyFinancialInterests(affiliations)
        );
    }


}
