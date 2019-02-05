package org.innovateuk.ifs.competitionsetup.projectdocument.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.ProjectDocumentForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_DOCUMENT_TITLE_HAS_BEEN_USED;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENT;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-document")
@SecuredBySpring(value = "Controller", description = "Only comp admin, project finance and IFS Admin can perform the below activities", securedType = CompetitionSetupDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupDocumentController {

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
    private CompetitionSetupDocumentRestService competitionSetupDocumentRestService;

    @Autowired
    private FileTypeRestService fileTypeRestService;

    @GetMapping("/landing-page")
    public String projectDocumentLandingPage(Model model,
                                             @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                             @ModelAttribute(LANDING_FORM_ATTR_NAME) LandingPageForm form,
                                             BindingResult bindingResult) {

        Redirect redirect = doViewProjectDocument(model, competitionId);
        return redirect.redirect ? redirect.url : "competition/setup";
    }

    private Redirect doViewProjectDocument(Model model, long competitionId) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Redirect redirect = new Redirect(false);

        if(competitionResource.isNonIfs()) {
            redirect.redirect = true;
            redirect.url = "redirect:/non-ifs-competition/setup/" + competitionId;
            return redirect;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            redirect.redirect = true;
            redirect.url = "redirect:/competition/setup/" + competitionResource.getId();
            return redirect;
        }

        model.addAttribute("model", competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT));

        return redirect;
    }

    @PostMapping("/landing-page")
    public String saveProjectDocumentLandingPage( @ModelAttribute(LANDING_FORM_ATTR_NAME) @Valid LandingPageForm form,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  Model model) {

        Supplier<String> failureView = () -> projectDocumentLandingPage(model, competitionId, form, bindingResult);
        Supplier<String> successView = () -> "redirect:/competition/setup/" + competitionId;

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            List<CompetitionDocumentResource> competitionDocumentResources =  competitionSetupDocumentRestService.findByCompetitionId(competitionId).getSuccess();
            competitionDocumentResources.forEach(projectDocumentResource -> enableOrDisableProjectDocument(projectDocumentResource, form.getEnabledIds()));

            RestResult<List<CompetitionDocumentResource>> updateResult = competitionSetupDocumentRestService.save(competitionDocumentResources);

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, successView);
                });
    }

    private void enableOrDisableProjectDocument(CompetitionDocumentResource competitionDocumentResource, Set<Long> enabledIds) {
        competitionDocumentResource.setEnabled(enabledIds != null && enabledIds.contains(competitionDocumentResource.getId()));
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

        ProjectDocumentForm form = createProjectDocumentForm(competitionSetupDocumentRestService.findOne(projectDocumentId).getSuccess());

        return doViewSaveProjectDocument(model, form);
    }

    private ProjectDocumentForm createProjectDocumentForm(CompetitionDocumentResource resource) {
        ProjectDocumentForm form = new ProjectDocumentForm(resource.getId(), resource.getTitle(), resource.getGuidance(),
                resource.isEditable(), resource.isEnabled());

        populateFileTypes(form, resource);

        return form;
    }

    private void populateFileTypes(ProjectDocumentForm form, CompetitionDocumentResource resource) {

        resource.getFileTypes().forEach(fileTypeId -> {
            FileTypeResource fileTypeResource = fileTypeRestService.findOne(fileTypeId).getSuccess();
            updateForm(form, fileTypeResource.getName());
        });
    }

    private void updateForm(ProjectDocumentForm form, String fileTypeName) {
        updateForm(form,"PDF", fileTypeName, form1 -> form1.setPdf(true));
        updateForm(form,"Spreadsheet", fileTypeName, form1 -> form1.setSpreadsheet(true));
    }

    private void updateForm(ProjectDocumentForm form, String fileType, String fileTypeName, Consumer<ProjectDocumentForm> fieldToSet) {
        if (fileType.equalsIgnoreCase(fileTypeName)) {
            fieldToSet.accept(form);
        }
    }

    @PostMapping("/save")
    public String saveProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                      Model model,
                                      @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDocumentForm form,
                                      BindingResult bindingResult, ValidationHandler validationHandler,
                                      UserResource loggedInUser) {

        Supplier<String> failureView = () -> saveProjectDocumentFailureView(competitionId, model, form);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            CompetitionDocumentResource competitionDocumentResource = createProjectDocumentResource(form, competitionId);
            RestResult<CompetitionDocumentResource> updateResult = competitionSetupDocumentRestService.save(competitionDocumentResource);

            return validationHandler.addAnyErrors(updateResult,
                                                    mappingErrorKeyToField(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE, "acceptedFileTypesId"),
                                                    mappingErrorKeyToField(PROJECT_DOCUMENT_TITLE_HAS_BEEN_USED, "title"),
                                                    fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId));
        });
    }

    private String saveProjectDocumentFailureView(long competitionId, Model model, ProjectDocumentForm form) {
        Redirect redirect = doViewProjectDocument(model, competitionId);
        return redirect.redirect ? redirect.url : doViewSaveProjectDocument(model, form);
    }

    private CompetitionDocumentResource createProjectDocumentResource(ProjectDocumentForm form, long competitionId) {
        CompetitionDocumentResource competitionDocumentResource = new CompetitionDocumentResource(competitionId, form.getTitle(), form.getGuidance(),
                form.isEditable(), form.isEnabled(), populateFileTypes(form));

        if (form.getProjectDocumentId() != null) {
            competitionDocumentResource.setId(form.getProjectDocumentId());
        }

        return competitionDocumentResource;
    }

    private List<Long> populateFileTypes(ProjectDocumentForm form) {

        List<Long> fileTypes = new ArrayList<>();
        populateFileType(fileTypes, "PDF", form.isPdf());
        populateFileType(fileTypes, "Spreadsheet", form.isSpreadsheet());

        return fileTypes;
    }

    private void populateFileType(List<Long> fileTypes, String fileType, boolean populate) {
        if (populate) {
            FileTypeResource fileTypeResource = fileTypeRestService.findByName(fileType).getSuccess();
            fileTypes.add(fileTypeResource.getId());
        }
    }

    @PostMapping("/{projectDocumentId}/delete")
    public String deleteProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("projectDocumentId") long projectDocumentId,
                                          Model model) {

        competitionSetupDocumentRestService.delete(projectDocumentId);
        return format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId);
    }
}
