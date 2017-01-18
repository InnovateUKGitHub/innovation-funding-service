package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller serves the Eligibility page where internal users can confirm the viability of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OpenFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private CompetitionService competitionService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = GET)
    public String viewEligibility(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpServletRequest request) {
        return doViewEligibility(projectId, organisationId, form , bindingResult, model, request);
    }

    private String doViewEligibility(Long projectId, Long organisationId, ApplicationForm form, BindingResult bindingResult, Model model, HttpServletRequest request) {
        model.addAttribute("model", getViewModel(projectId, organisationId, model));

        poulateProjectFinanceDetails(projectId, organisationId, form, bindingResult, model, request);

        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityViewModel getViewModel(Long projectId, Long organisationId, Model model) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        FinanceCheckEligibilityResource eligibility = financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId);

        return new FinanceChecksEligibilityViewModel(eligibility, organisation.getName(), project.getName(), application.getFormattedId(), leadPartnerOrganisation, project.getId());
    }

    private void poulateProjectFinanceDetails(Long projectId, Long organisationId, ApplicationForm form, BindingResult bindingResult, Model model, HttpServletRequest request){
        ProjectResource project = projectService.getById(projectId);
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(project.getApplication());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(FINANCE)).get(0);

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);

        openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId, true, true);

        model.addAttribute("project", project);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form) {
        //organisationDetailsModelPopulator.populateModel(model, application.getId());
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }
}
