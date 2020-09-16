package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoCompleteSectionsUtil {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private SectionStatusService sectionStatusService;

    public void intitialiseCompleteSectionsForOrganisation(Application application, long organisationId, long processRoleId) {
        Competition competition = application.getCompetition();
        OrganisationResource lead = organisationService.findById(organisationId).getSuccess();
        competition.getSections().stream()
                .filter(section -> section.getType().isSectionTypeNotRequiredForOrganisationAndCompetition(competition, lead.getOrganisationTypeEnum(), application.getLeadOrganisationId().equals(organisationId)))
                .forEach(section -> sectionStatusService.markSectionAsNotRequired(section.getId(), application.getId(), processRoleId));

    }
}
