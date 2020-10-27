package org.innovateuk.ifs.project.projectteam.controller;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.projectdetails.form.FinanceContactForm;
import org.innovateuk.ifs.project.projectteam.viewmodel.FinanceContactViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This controller will handle all requests that are related to the finance contact.
 */
@Controller
@RequestMapping("/project")
public class FinanceContactController {

    private ProjectService projectService;
    private ProjectDetailsService projectDetailsService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public FinanceContactController(
            ProjectService projectService,
            ProjectDetailsService projectDetailsService
    ) {
        this.projectService = projectService;
        this.projectDetailsService = projectDetailsService;
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @GetMapping("/{projectId}/team/finance-contact/organisation/{organisationId}")
    public String viewFinanceContact(@PathVariable("projectId") final long projectId,
                                     @PathVariable("organisationId") final long organisationId,
                                     Model model,
                                     @ModelAttribute(name = "form", binding = false) FinanceContactForm financeContactForm,
                                     UserResource loggedInUser) {
        populateOriginalFinanceContactForm(projectId, organisationIdm financeContactForm);
        return doViewFinanceContact(model, projectId, organisationId, loggedInUser, financeContactForm);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CONTACT_PAGE')")
    @PostMapping(value = "/{projectId}/team/finance-contact/organisation/{organisationId}")
    public String updateFinanceContact(@PathVariable("projectId") final long projectId,
                                       @PathVariable("organisationId") final long organisationId,
                                       Model model,
                                       @Valid @ModelAttribute("form") FinanceContactForm financeContactForm,
                                       @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                       UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewFinanceContact(model, projectId, organisationId, loggedInUser, financeContactForm);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = projectDetailsService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactForm.getFinanceContact());

            return validationHandler.addAnyErrors(updateResult, toField("financeContact")).
                    failNowOrSucceedWith(failureView, () -> redirectToProjectTeamPage(projectId));
        });
    }

    private void populateOriginalFinanceContactForm(final long projectId, long organisationId, FinanceContactForm financeContactForm) {
        Optional<ProjectUserResource> existingFinanceContact = getFinanceContact(projectId, organisationId);
        financeContactForm.setFinanceContact(existingFinanceContact.map(ProjectUserResource::getUser).orElse(null));
    }

    private Optional<ProjectUserResource> getFinanceContact(final long projectId, final long organisationId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> FINANCE_CONTACT.getId() == pu.getRole() && pu.getOrganisation().equals(organisationId));
    }

    private String doViewFinanceContact(Model model, long projectId, long organisationId, UserResource loggedInUser, FinanceContactForm form) {

        List<ProjectUserResource> organisationProjectUsers = simpleFilter(projectService.getProjectUsersWithPartnerRole(projectId),
                                                                        pu -> pu.getOrganisation() == organisationId);

        if(!userIsPartnerInOrganisationForProject(organisationProjectUsers, loggedInUser)) {
            return redirectToProjectTeamPage(projectId);
        }

        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        FinanceContactViewModel viewModel = new FinanceContactViewModel(organisationProjectUsers, projectId, projectResource.getName(), competition.isLoan());

        model.addAttribute("form", form);
        model.addAttribute("model", viewModel);
        return "project/team/finance-contact";

    }

    private boolean userIsPartnerInOrganisationForProject(List<ProjectUserResource> projectUsers,
                                                          UserResource loggedInUser) {
        return simpleAnyMatch(projectUsers,
                              pu -> pu.getUser().equals(loggedInUser.getId()));
    }

    private String redirectToProjectTeamPage(long projectId) {
        return "redirect:/project/" + projectId + "/team";
    }
}

