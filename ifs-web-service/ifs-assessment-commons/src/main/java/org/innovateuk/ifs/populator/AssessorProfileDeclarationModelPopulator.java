package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
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
    private CompetitionRestService competitionRestService;

    public AssessorProfileDeclarationModelPopulator(AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator,
                                                    AffiliationRestService affiliationRestService,
                                                    CompetitionRestService competitionRestService) {
        this.assessorProfileDetailsModelPopulator = assessorProfileDetailsModelPopulator;
        this.affiliationRestService = affiliationRestService;
        this.competitionRestService = competitionRestService;
    }

    public AssessorProfileDeclarationViewModel populateModel(UserResource user, ProfileResource profile, Optional<Long> competitionId, String originQuery, boolean compAdminUser) {

        CompetitionResource competition =
                Optional.ofNullable(competitionId).isPresent() ? competitionRestService.getCompetitionById(competitionId.get()).getSuccess() : null;

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
                originQuery,
                compAdminUser
        );
    }


}
