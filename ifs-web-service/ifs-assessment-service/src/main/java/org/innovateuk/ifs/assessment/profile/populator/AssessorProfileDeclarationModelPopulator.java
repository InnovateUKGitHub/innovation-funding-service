package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Build the model for the Assessor Declaration of Interest read-only view.
 */
@Component
public class AssessorProfileDeclarationModelPopulator extends AssessorProfileDeclarationBasePopulator {

    @Autowired
    private AffiliationRestService affiliationRestService;

    public AssessorProfileDeclarationViewModel populateModel(UserResource user) {
        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(affiliationRestService.getUserAffiliations(user.getId()).getSuccessObjectOrThrowException());

        Optional<AffiliationResource> principalEmployer = getPrincipalEmployer(affiliations);

        return new AssessorProfileDeclarationViewModel(
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
