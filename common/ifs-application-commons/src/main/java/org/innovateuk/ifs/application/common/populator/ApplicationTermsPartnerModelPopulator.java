package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static java.util.stream.Collectors.toList;

@Component
public class ApplicationTermsPartnerModelPopulator {

    private final SectionService sectionService;
    private final ProcessRoleRestService processRoleRestService;
    private final OrganisationService organisationService;
    @Autowired
    private CompetitionRestService competitionRestService;

    public ApplicationTermsPartnerModelPopulator(SectionService sectionService,
                                                 ProcessRoleRestService processRoleRestService,
                                                 OrganisationService organisationService) {
        this.sectionService = sectionService;
        this.processRoleRestService = processRoleRestService;
        this.organisationService = organisationService;
    }

    public ApplicationTermsPartnerViewModel populate(ApplicationResource application, long questionId) {
        long termsAndConditionsSectionId = sectionService.getTermsAndConditionsSection(application.getCompetition()).getId();
        List<ProcessRoleResource> userApplicationRoles = processRoleRestService.findProcessRole(application.getId()).getSuccess();
        SortedSet<OrganisationResource> organisations = organisationService.getApplicationOrganisations(userApplicationRoles);

        long leadOrganisationId = userApplicationRoles
                .stream()
                .filter(pr -> pr.getRole() == ProcessRoleType.LEADAPPLICANT)
                .findFirst()
                .map(ProcessRoleResource::getOrganisationId)
                .orElseThrow(() -> new IFSRuntimeException("Lead organisation not found for application " + application.getId()));

        List<Long> acceptedOrgs = sectionService.getCompletedSectionsByOrganisation(application.getId())
                .entrySet()
                .stream()
                .filter(t -> t.getValue().contains(termsAndConditionsSectionId))
                .map(Map.Entry::getKey)
                .collect(toList());

        List<ApplicationTermsPartnerRowViewModel> partners = organisations
                .stream()
                .map(o -> new ApplicationTermsPartnerRowViewModel(
                        o.getName(),
                        o.getId() == leadOrganisationId,
                        acceptedOrgs.contains(o.getId()))
                )
                .sorted((o1, o2) -> o1.isLead() ? -1 : 1)
                .collect(toList());

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        return new ApplicationTermsPartnerViewModel(application.getId(), application.getCompetitionName(), questionId, partners,
                competition.getTermsAndConditions().isProcurementThirdParty(), competition.getCompetitionThirdPartyConfigResource());
    }
}