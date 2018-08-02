package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.profile.populator.AssessorProfileDeclarationBasePopulator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessor.viewmodel.CompAssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Build the model for Assessors' Profile declaration view.
 */
@Component
public class CompAssessorProfileDeclarationModelPopulator extends AssessorProfileDeclarationBasePopulator {

    private AffiliationRestService affiliationRestService;
    private CompetitionRestService competitionRestService;
    private AssessorRestService assessorRestService;

    public CompAssessorProfileDeclarationModelPopulator(AffiliationRestService affiliationRestService,
                                                        CompetitionRestService competitionRestService,
                                                        AssessorRestService assessorRestService) {
        this.affiliationRestService = affiliationRestService;
        this.competitionRestService = competitionRestService;
        this.assessorRestService = assessorRestService;
    }

    public CompAssessorProfileDeclarationViewModel populateModel(long assessorId, long competitionId, String originQuery) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();

        UserResource user = assessorProfile.getUser();
        ProfileResource profile = assessorProfile.getProfile();

        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(affiliationRestService.getUserAffiliations(user.getId()).getSuccess().getAffiliationResourceList());

        Optional<AffiliationResource> principalEmployer = getPrincipalEmployer(affiliations);

        return new CompAssessorProfileDeclarationViewModel(
                competition,
                assessorId,
                user.getName(),
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
