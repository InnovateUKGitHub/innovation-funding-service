package org.innovateuk.ifs.competitionsetup.documents.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.DocumentResource;
import org.innovateuk.ifs.competitionsetup.application.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.innovateuk.ifs.competitionsetup.documents.populator.DocumentEditFormPopulator;
import org.innovateuk.ifs.competitionsetup.documents.populator.DocumentEditModelPopulator;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.DOCUMENTS;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;

import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Document section and it's sub-sections in the
 * competition setup process
 */


@Controller
@RequestMapping("/competition/setup/{competitionId}/section/documents")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupDocumentsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupDocumentsController {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupDocumentsController.class);
    public static final String DOCUMENTS_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/documents/landing-page";
    private static final String MODEL = "model";
    private static final String NEW_DOCUMENT_TAG = "new_document";
    private static final String editView = "competition/setup/document";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;


    @PostMapping(value = "/landing-page", params = "createDocument")
    public String createDocument(@PathVariable(COMPETITION_ID_KEY) long competitionId) {
        // redirect to new
        return String.format("redirect:/competition/setup/%d/section/documents/document/add", competitionId);
    }

    @PostMapping("/landing-page")
    public String setDocumentProcessAsComplete(Model model,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  @ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm form,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        return "redirect:/competition/setup/" + competitionId;   

        // TODO: actually set process as complete, also update included checkboxes
    }


    @GetMapping("/landing-page")
    public String documentsLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        // TODO: these checks are repeated throughout should they be librarizied?
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        // TODO: we populate a modelview, this seems to be enough for the langing page, do we need a form as well?
        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, DOCUMENTS));
        return "competition/setup";
    }

    @GetMapping("/document/{documentId}/edit")
    public String editDocumentInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("documentId") Long documentId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        // TODO: these checks are repeated throughout should they be librarizied?
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        model.addAttribute(NEW_DOCUMENT_TAG, Boolean.FALSE);
        return getDocumentPage(model, competitionResource, competitionId, documentId, true, null);
    }

    @GetMapping("/document/add")
    public String addDocumentInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        // TODO: these checks are repeated throughout should they be librarizied?
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        model.addAttribute(NEW_DOCUMENT_TAG, Boolean.TRUE);
        return getDocumentPage(model, competitionResource, competitionId, null, true, null);
    }

    @PostMapping("/document/add")
    public String saveNewDocumentInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          Model model) {

        // TODO: create new row
        return String.format("redirect:/competition/setup/%d/section/documents/landing-page", competitionId);
    }

    @PostMapping("/document/{documentId}/edit", params = "removeDocument")
    public String removeDocumentInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                            @PathVariable("documentId") Long documentId,
                                            Model model) {

        // TODO: delete old row
        model.addAttribute(NEW_DOCUMENT_TAG, Boolean.FALSE);
        return String.format("redirect:/competition/setup/%d/section/documents/landing-page", competitionId);
    }

    @PostMapping("/document/{documentId}/edit")
    public String saveDocumentInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("documentId") Long documentId,
                                          Model model) {

        // TODO: save over old row
        return String.format("redirect:/competition/setup/%d/section/documents/landing-page", competitionId);
    }

    public String getDocumentPage(Model model, CompetitionResource competitionResource, long competitionId, Long documentId, Boolean isEditable, CompetitionSetupForm form) {

        //TODO: can't do this, need to create a new setup system for populators???
        DocumentEditModelPopulator modelPopulator = new DocumentEditModelPopulator();
        DocumentEditFormPopulator formPopulator = new DocumentEditFormPopulator();

        //basically seems to be a big wrapper for the document...
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, formPopulator.populateForm(documentId));

        //TODO: what do we get from this? edittable? need to fix.
        model.addAttribute(MODEL, modelPopulator.populateModel(competitionResource));
        model.addAttribute(COMPETITION_ID_KEY, competitionId);
        return editView;
    }

    //TODO: handle saving from edit page
//    @PostMapping("/document/{documentId}/edit")
//    public String submitDocumentChanges(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) DocumentForm competitionSetupForm,
//                                         BindingResult bindingResult,
//                                         ValidationHandler validationHandler,
//                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
//                                         Model model) {
//
//        // save and redirect
//    }
}
