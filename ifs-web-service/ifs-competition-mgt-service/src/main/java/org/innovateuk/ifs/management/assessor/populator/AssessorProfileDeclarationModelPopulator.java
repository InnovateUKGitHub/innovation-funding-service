package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessor.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.profile.populator.AssessorProfileDeclarationBasePopulator;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Build the model for Assessors' Profile view.
 */
@Component
public class AssessorProfileDeclarationModelPopulator extends AssessorProfileDeclarationBasePopulator {

    private AffiliationRestService affiliationRestService;
    private CompetitionService competitionService;
    private AssessorRestService assessorRestService;

    public AssessorProfileDeclarationModelPopulator(AffiliationRestService affiliationRestService,
                                                    CompetitionService competitionService,
                                                    AssessorRestService assessorRestService) {
        this.affiliationRestService = affiliationRestService;
        this.competitionService = competitionService;
        this.assessorRestService = assessorRestService;
    }

    public AssessorProfileDeclarationViewModel populateModel(long assessorId, long competitionId, String originQuery) {

        CompetitionResource competition = competitionService.getById(competitionId);
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();

        UserResource user = assessorProfile.getUser();
        ProfileResource profile = assessorProfile.getProfile();

        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(affiliationRestService.getUserAffiliations(user.getId()).getSuccess().getAffiliationResourceList());

        Optional<AffiliationResource> principalEmployer = getPrincipalEmployer(affiliations);

        return new AssessorProfileDeclarationViewModel(
                competition,
                assessorId,
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                profile.getAddress(),
                Optional.ofNullable(profile.getBusinessType()).map(BusinessType::getDisplayName).orElse(null),
                originQuery,
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
