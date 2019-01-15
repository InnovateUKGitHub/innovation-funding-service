package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

/**
 * The Controller for the "Your organisation" page in the Application Form process.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}")
public class YourOrganisationController {

    private YourOrganisationRestService yourOrganisationRestService;

    @Autowired
    YourOrganisationController(YourOrganisationRestService yourOrganisationRestService) {
        this.yourOrganisationRestService = yourOrganisationRestService;
    }

    // for ByteBuddy
    YourOrganisationController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("competitionId") long competitionId,
            @PathVariable("organisationId") long organisationId,
            @PathVariable("sectionId") long sectionId) {

        boolean includeGrowthTable = yourOrganisationRestService.isIncludingGrowthTable(competitionId).getSuccess();

        return redirectToViewPage(applicationId, competitionId, organisationId, sectionId, includeGrowthTable);
    }

    private String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId, boolean includeGrowthTable) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/competition/%d/organisation/%d/section/%d/%s",
                        applicationId,
                        competitionId,
                        organisationId,
                        sectionId,
                        includeGrowthTable ? "with-growth-table" : "without-growth-table");
    }
}
