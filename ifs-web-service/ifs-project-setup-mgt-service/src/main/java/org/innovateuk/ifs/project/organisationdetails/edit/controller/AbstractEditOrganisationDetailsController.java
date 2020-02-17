package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.edit.viewmodel.ProjectOrganisationSizeViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

public abstract class AbstractEditOrganisationDetailsController<F> {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "READ", description = "Ifs Admin and Project finance users can view edit organisation size page")
    public String view(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            Model model) {

        model.addAttribute("model", getViewModel(projectId, organisationId));
        model.addAttribute("form", form(projectId, organisationId));

        return view();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'ifs_administrator')")
    @SecuredBySpring(value = "UPDATE_ORGANISATION_FUNDING_DETAILS", description = "Internal users can update organisation funding details")
    public String save(
            @PathVariable long projectId,
            @PathVariable long organisationId,
            @Valid @ModelAttribute("form") F form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model) {

        Supplier<String> failureHandler = () -> {
            model.addAttribute("model", getViewModel(projectId, organisationId));
            return view();
        };
        Supplier<String> successHandler = () -> redirectToOrganisationDetails(projectId, organisationId);

        return validationHandler.failNowOrSucceedWith(failureHandler,() -> {
             validationHandler.addAnyErrors(update(projectId, organisationId, form));
             return validationHandler.failNowOrSucceedWith(failureHandler, successHandler);
        });
    }

    private ProjectOrganisationSizeViewModel getViewModel(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        return new ProjectOrganisationSizeViewModel(project,
                organisation.getName(),
                organisationId,
                false,
                false,
                false,
                false,
                competition.isProcurement());
    }

    protected abstract String redirectToOrganisationDetails(long projectId, long organisationId);

    protected abstract String view();

    protected abstract F form(long projectId, long organisationId);

    protected abstract ServiceResult<Void> update(long projectId,
                                                  long organisationId,
                                                  F form);
}