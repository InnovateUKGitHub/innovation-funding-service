package org.innovateuk.ifs.competitionsetup.projectdocument.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupProjectDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.ProjectDocumentForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENT;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.mappingErrorKeyToField;

@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-document")
@SecuredBySpring(value = "Controller", description = "Only comp admin, project finance and IFS Admin can perform the below activities", securedType = CompetitionSetupProjectDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupProjectDocumentController {

    public static final String PROJECT_DOCUMENT_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/project-document/landing-page";
    private static final String FORM_ATTR_NAME = "form";
    private static final String LANDING_FORM_ATTR_NAME = "landingPageForm";

    private class Redirect {
        private boolean redirect;
        private String url;

        private Redirect(boolean redirect) {
            this.redirect = redirect;
        }
    }

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupProjectDocumentRestService competitionSetupProjectDocumentRestService;

    @GetMapping("/landing-page")
    public String projectDocumentLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {

        Redirect redirect = doViewProjectDocument(model, competitionId);

        return redirect.redirect ? redirect.url : "competition/setup";

    }

    private Redirect doViewProjectDocument(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Redirect redirect = new Redirect(false);

        if(competitionResource.isNonIfs()) {
            redirect.redirect = true;
            redirect.url = "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            redirect.redirect = true;
            redirect.url = "redirect:/competition/setup/" + competitionResource.getId();
        }

        model.addAttribute("model", competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT));
        model.addAttribute(LANDING_FORM_ATTR_NAME, new LandingPageForm());

        return redirect;
    }

    @PostMapping("/landing-page")
    public String saveProjectDocumentLandingPage( @ModelAttribute(LANDING_FORM_ATTR_NAME) LandingPageForm form,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  Model model) {

        Supplier<String> failureView = () -> projectDocumentLandingPage(model, competitionId);
        Supplier<String> successView = () -> "redirect:/competition/setup/" + competitionId;

        List<ProjectDocumentResource> projectDocumentResources =  competitionSetupProjectDocumentRestService.findByCompetitionId(competitionId).getSuccess();
        projectDocumentResources.forEach(projectDocumentResource -> enableOrDisableProjectDocument(projectDocumentResource, form.getEnabledIds()));

        RestResult<List<ProjectDocumentResource>> updateResult = competitionSetupProjectDocumentRestService.save(projectDocumentResources);

        return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                failNowOrSucceedWith(failureView, successView);
    }

    private void enableOrDisableProjectDocument(ProjectDocumentResource projectDocumentResource, Set<Long> enabledIds) {
        projectDocumentResource.setEnabled(enabledIds != null && enabledIds.contains(projectDocumentResource.getId()));
    }

    @GetMapping("/add")
    public String viewAddProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     Model model) {

        Redirect redirect = doViewProjectDocument(model, competitionId);

        ProjectDocumentForm form = new ProjectDocumentForm(true, true);
        return redirect.redirect ? redirect.url : doViewSaveProjectDocument(model, form);
    }

    private String doViewSaveProjectDocument(Model model, ProjectDocumentForm form) {
        model.addAttribute(FORM_ATTR_NAME, form);
        return "competition/setup/save-project-document";
    }

    @GetMapping("/{projectDocumentId}/edit")
    public String viewEditProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("projectDocumentId") long projectDocumentId,
                                         Model model) {

        Redirect redirect = doViewProjectDocument(model, competitionId);
        return redirect.redirect ? redirect.url : doViewEditProjectDocument(model, projectDocumentId);
    }

    private String doViewEditProjectDocument(Model model, long projectDocumentId) {

        ProjectDocumentForm form = createProjectDocumentForm(competitionSetupProjectDocumentRestService.findOne(projectDocumentId).getSuccess());

        return doViewSaveProjectDocument(model, form);
    }

    @PostMapping("/save")
    public String saveProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                      Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDocumentForm form,
                                      BindingResult bindingResult, ValidationHandler validationHandler,
                                      UserResource loggedInUser) {

        Supplier<String> failureView = () -> saveProjectDocumentFailureView(competitionId, model, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ProjectDocumentResource projectDocumentResource = createProjectDocumentResource(form, competitionId);
            RestResult<ProjectDocumentResource> updateResult = competitionSetupProjectDocumentRestService.save(projectDocumentResource);

            return validationHandler.addAnyErrors(updateResult,
                                                    mappingErrorKeyToField(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE, "acceptedFileTypesId"),
                                                    fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId));
        });
    }

    private String saveProjectDocumentFailureView(long competitionId, Model model, ProjectDocumentForm form) {
        Redirect redirect = doViewProjectDocument(model, competitionId);
        return redirect.redirect ? redirect.url : doViewSaveProjectDocument(model, form);
    }

    private ProjectDocumentResource createProjectDocumentResource(ProjectDocumentForm form, long competitionId) {
        ProjectDocumentResource projectDocumentResource = new ProjectDocumentResource(competitionId, form.getTitle(), form.getGuidance(),
                form.isEditable(), form.isEnabled(), form.isPdf(), form.isSpreadsheet());

        if (form.getProjectDocumentId() != null) {
            projectDocumentResource.setId(form.getProjectDocumentId());
        }
        return projectDocumentResource;
    }

    private ProjectDocumentForm createProjectDocumentForm(ProjectDocumentResource resource) {
        return new ProjectDocumentForm(resource.getId(), resource.getTitle(), resource.getGuidance(),
                resource.isEditable(), resource.isEnabled(), resource.isPdf(), resource.isSpreadsheet());
    }

    @PostMapping("/{projectDocumentId}/delete")
    public String deleteProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("projectDocumentId") long projectDocumentId,
                                          Model model) {

        competitionSetupProjectDocumentRestService.delete(projectDocumentId);
        return format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId);
    }
}
