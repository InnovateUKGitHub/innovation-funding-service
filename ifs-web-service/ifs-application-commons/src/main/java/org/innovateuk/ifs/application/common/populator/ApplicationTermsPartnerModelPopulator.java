package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static java.util.stream.Collectors.toList;

@Component
public class ApplicationTermsPartnerModelPopulator {

    private ApplicationRestService applicationRestService;
    private SectionService sectionService;
    private UserRestService userRestService;
    private OrganisationService organisationService;

    public ApplicationTermsPartnerModelPopulator(ApplicationRestService applicationRestService,
                                                 SectionService sectionService,
                                                 UserRestService userRestService,
                                                 OrganisationService organisationService) {
        this.applicationRestService = applicationRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
        this.organisationService = organisationService;
    }

    public ApplicationTermsPartnerViewModel populate(long applicationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        long termsAndConditionsSectionId = sectionService.getTermsAndConditionsSection(application.getCompetition()).getId();
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        SortedSet<OrganisationResource> organisations = organisationService.getApplicationOrganisations(userApplicationRoles);

        long leadOrganisationId = userApplicationRoles
                .stream()
                .filter(t -> t.getRole() == Role.LEADAPPLICANT)
                .findFirst()
                .map(ProcessRoleResource::getOrganisationId)
                .get();

        List<Long> orgSections = sectionService.getCompletedSectionsByOrganisation(applicationId)
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
                        orgSections.contains(termsAndConditionsSectionId))
                )
                .sorted((o1, o2) -> o1.isLead() ? 1 : 0)
                .collect(toList());

        return new ApplicationTermsPartnerViewModel(applicationId, partners);
    }
}