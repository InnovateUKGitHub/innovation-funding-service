package org.innovateuk.ifs.competitionsetup.projectdocuments.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupProjectDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.documents.controller.CompetitionSetupDocumentsController;
import org.innovateuk.ifs.competitionsetup.projectdocuments.form.ProjectDocumentForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENTS;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;

@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-documents")
@SecuredBySpring(value = "Controller", description = "Only comp admin, project finance and IFS Admin can perform the below activities", securedType = CompetitionSetupDocumentsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupProjectDocumentsController {

    public static final String PROJECT_DOCUMENTS_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/project-documents/landing-page";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupProjectDocumentRestService competitionSetupProjectDocumentRestService;

    @GetMapping("/landing-page")
    public String projectDocumentsLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {

        String redirect = doViewProjectDocuments(model, competitionId);

        return redirect != null ? redirect : "competition/setup";

    }

    private String doViewProjectDocuments(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        model.addAttribute("model", competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENTS));

        return null;
    }

    @GetMapping("/add")
    public String viewAddProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     Model model) {

        String redirect = doViewProjectDocuments(model, competitionId);
        return redirect != null ? redirect : doViewAddProjectDocument(model);
    }

    private String doViewAddProjectDocument(Model model) {
        model.addAttribute(FORM_ATTR_NAME, new ProjectDocumentForm());
        return "competition/setup/add-project-document";
    }

    @PostMapping("/add")
    public String addProjectDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     Model model,
                                     @Valid @ModelAttribute(FORM_ATTR_NAME) ProjectDocumentForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                     UserResource loggedInUser) {

        ProjectDocumentResource projectDocumentResource = createProjectDocumentResource(form, true, competitionId);
        competitionSetupProjectDocumentRestService.save(projectDocumentResource);

        return projectDocumentsLandingPage(model, competitionId);
    }

    private ProjectDocumentResource createProjectDocumentResource(ProjectDocumentForm form, boolean enabled, long competitionId) {

        return new ProjectDocumentResource(competitionId, form.getTitle(), form.getGuidance(), enabled, form.isPdf(), form.isSpreadsheet());

    }
}
