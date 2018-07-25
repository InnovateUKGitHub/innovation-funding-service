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

        return projectDocumentLandingPage(model, competitionId);
    }

    private ProjectDocumentResource createProjectDocumentResource(ProjectDocumentForm form, boolean enabled, long competitionId) {

        return new ProjectDocumentResource(competitionId, form.getTitle(), form.getGuidance(), enabled, form.isPdf(), form.isSpreadsheet());

    }
}
