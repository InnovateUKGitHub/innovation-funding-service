package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationsize.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/with-growth-table")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ProjectOrganisationSizeWithGrowthTableController.class)
public class ProjectOrganisationSizeWithGrowthTableController {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    public String editOrganisationSize(
                                       @PathVariable long projectId,
                                       @PathVariable long organisationId,
                                       Model model) {

        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", formRequest(projectId, organisationId));

        return "project/edit-organisation-size-with-growth-table";
    }

    @PostMapping(params = "save-and-return")
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "UPDATE_ORGANISATION_FUNDING_DETAILS", description = "Internal users can update organisation funding details")
    public String saveWithGrowthTable(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            @ModelAttribute YourOrganisationWithGrowthTableForm form) {

        updateYourOrganisationWithGrowthTable(projectId, organisationId, form);
        return redirectToOrganisationDetails(projectId, organisationId);
    }

    private ProjectOrganisationSizeViewModel getViewModel(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return new ProjectOrganisationSizeViewModel(project,
                organisation.getName(),
                organisationId,
                financesWithGrowthTable.getOrganisationSize(),
                financesWithGrowthTable.getAnnualTurnoverAtLastFinancialYear(),
                financesWithGrowthTable.getHeadCountAtLastFinancialYear(),
                false,
                false,
                false,
                false);
    }

    private YourOrganisationWithGrowthTableForm formRequest(long projectId, long organisationId) {
        OrganisationFinancesWithGrowthTableResource financesWithGrowthTable = projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId).getSuccess();
        return withGrowthTableFormPopulator.populate(financesWithGrowthTable);
    }

    private void updateYourOrganisationWithGrowthTable(long projectId,
                                                       long organisationId,
                                                       YourOrganisationWithGrowthTableForm form) {

        OrganisationFinancesWithGrowthTableResource finances = new OrganisationFinancesWithGrowthTableResource(
                form.getOrganisationSize(),
                form.getFinancialYearEnd(),
                form.getHeadCountAtLastFinancialYear(),
                form.getAnnualTurnoverAtLastFinancialYear(),
                form.getAnnualProfitsAtLastFinancialYear(),
                form.getAnnualExportAtLastFinancialYear(),
                form.getResearchAndDevelopmentSpendAtLastFinancialYear());

        projectYourOrganisationRestService.updateOrganisationFinancesWithGrowthTable(projectId, organisationId, finances).getSuccess();
    }

    private String redirectToOrganisationDetails(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return "redirect:" + String.format("/competition/%d/project/%d/organisation/%d/details", project.getCompetition(), projectId, organisationId);
    }
}