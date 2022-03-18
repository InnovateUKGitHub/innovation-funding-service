package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AssessorProfileDeclarationModelPopulator extends AssessorProfileDeclarationBasePopulator {

    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;
    private AffiliationRestService affiliationRestService;
    private CompetitionRestService competitionRestService;
    @Value("${ifs.edi.update.enabled}")
    private boolean isEdiUpdateEnabled;
    @Value("${ifs.edi.salesforce.page.url}")
    private String ediUpdateUrl;
    public AssessorProfileDeclarationModelPopulator(AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator,
                                                    AffiliationRestService affiliationRestService,
                                                    CompetitionRestService competitionRestService) {
        this.assessorProfileDetailsModelPopulator = assessorProfileDetailsModelPopulator;
        this.affiliationRestService = affiliationRestService;
        this.competitionRestService = competitionRestService;
    }

    public AssessorProfileDeclarationViewModel populateModel(UserResource user, ProfileResource profile, Optional<Long> competitionId, boolean compAdminUser) {
        isEdiUpdateEnabled = isEdiUpdateEnabled &&  user.hasRoles(ASSESSOR, APPLICANT);
        CompetitionResource competition = competitionId.map(id -> competitionRestService.getCompetitionById(id).getSuccess())
                .orElse(null);

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = assessorProfileDetailsModelPopulator.populateModel(user, profile);

        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(affiliationRestService.getUserAffiliations(user.getId()).getSuccess().getAffiliationResourceList());

        Optional<AffiliationResource> principalEmployer = getPrincipalEmployer(affiliations);

        return new AssessorProfileDeclarationViewModel(
                competition,
                assessorProfileDetailsViewModel,
                affiliations.size() > 0,
                principalEmployer.map(AffiliationResource::getOrganisation).orElse(null),
                principalEmployer.map(AffiliationResource::getPosition).orElse(null),
                getProfessionalAffiliations(affiliations),
                getAppointments(affiliations),
                getFinancialInterests(affiliations),
                getFamilyAffiliations(affiliations),
                getFamilyFinancialInterests(affiliations),
                compAdminUser,
                isEdiUpdateEnabled,
                user.getEdiStatus(),
                user.getEdiReviewDate(),
                ediUpdateUrl
        );
    }
}
