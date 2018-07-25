package org.innovateuk.ifs.competitionsetup.projectdocument.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupProjectDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
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

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENT;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;

@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-document")
@SecuredBySpring(value = "Controller", description = "Only comp admin, project finance and IFS Admin can perform the below activities", securedType = CompetitionSetupProjectDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupProjectDocumentController {

    public static final String PROJECT_DOCUMENT_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/project-document/landing-page";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupProjectDocumentRestService competitionSetupProjectDocumentRestService;

    @GetMapping("/landing-page")
    public String projectDocumentLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {

        String redirect = doViewProjectDocument(model, competitionId);

        return redirect != null ? redirect : "competition/setup";

    }

    private String doViewProjectDocument(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        model.addAttribute("model", competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT));

        return null;
    }

    @GetMapping("/add")
    public String viewAddProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     Model model) {

        String redirect = doViewProjectDocument(model, competitionId);

        ProjectDocumentForm form = new ProjectDocumentForm();
        form.setEnabled(true);
        return redirect != null ? redirect : doViewSaveProjectDocument(model, form);
    }

    private String doViewSaveProjectDocument(Model model, ProjectDocumentForm form) {
        model.addAttribute(FORM_ATTR_NAME, form);
        return "competition/setup/save-project-document";
    }

    @GetMapping("/{projectDocumentId}/edit")
    public String viewEditProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("projectDocumentId") long projectDocumentId,
                                         Model model) {

        String redirect = doViewProjectDocument(model, competitionId);
        return redirect != null ? redirect : doViewEditProjectDocument(model, projectDocumentId);
    }

    private String doViewEditProjectDocument(Model model, long projectDocumentId) {

        ProjectDocumentForm form = createProjectDocumentForm(competitionSetupProjectDocumentRestService.findOne(projectDocumentId).getSuccess());

        return doViewSaveProjectDocument(model, form);
    }

    @PostMapping("/save")
    public String saveProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     Model model,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDocumentForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                     UserResource loggedInUser) {

        ProjectDocumentResource projectDocumentResource = createProjectDocumentResource(form, competitionId);
        competitionSetupProjectDocumentRestService.save(projectDocumentResource);

        return projectDocumentLandingPage(model, competitionId);
    }

    private ProjectDocumentResource createProjectDocumentResource(ProjectDocumentForm form, long competitionId) {

        ProjectDocumentResource projectDocumentResource = new ProjectDocumentResource(competitionId, form.getTitle(), form.getGuidance(), form.isEnabled(), form.isPdf(), form.isSpreadsheet());

        if (form.getProjectDocumentId() != null) {
            projectDocumentResource.setId(form.getProjectDocumentId());
        }

        return projectDocumentResource;

    }

    private ProjectDocumentForm createProjectDocumentForm(ProjectDocumentResource resource) {

        return new ProjectDocumentForm(resource.getId(), resource.getTitle(), resource.getGuidance(), resource.isEnabled(), resource.isPdf(), resource.isSpreadsheet());

    }

    @DeleteMapping("/{projectDocumentId}")
    public String deleteProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("projectDocumentId") long projectDocumentId,
                                          Model model) {

        competitionSetupProjectDocumentRestService.delete(projectDocumentId);

        return projectDocumentLandingPage(model, competitionId);
    }
}
