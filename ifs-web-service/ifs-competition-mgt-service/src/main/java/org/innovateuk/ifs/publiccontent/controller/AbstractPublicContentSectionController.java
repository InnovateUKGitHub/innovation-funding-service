package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;

/**
 * Abstract controller for all public content sections.
 */
public abstract class AbstractPublicContentSectionController<M extends AbstractPublicContentViewModel, F extends AbstractPublicContentForm> {

    protected static final String TEMPLATE_FOLDER = "competition/";
    protected static final String FORM_ATTR_NAME = "form";
    private static final String COMPETITION_SETUP = "/competition/setup/";

    @Autowired
    protected PublicContentService publicContentService;

    @Autowired
    protected CompetitionRestService competitionRestService;

    @Autowired
    protected CompetitionSetupService competitionSetupService;

    protected abstract PublicContentViewModelPopulator<M> modelPopulator();
    protected abstract PublicContentFormPopulator<F> formPopulator();
    protected abstract PublicContentFormSaver<F> formSaver();

    @GetMapping("/{competitionId}")
    public String readOnly(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        return readOnly(competitionId, model, Optional.empty());
    }

    @GetMapping("/{competitionId}/edit")
    public String edit(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        return edit(competitionId, model, Optional.empty());
    }

    @PostMapping(value = "/{competitionId}/edit")
    public String markAsComplete(Model model,
                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                 @Valid @ModelAttribute(FORM_ATTR_NAME) F form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {
        return markAsComplete(competitionId, model, form, validationHandler);
    }

    protected String getPage(long competitionId, Model model, Optional<F> form, boolean readOnly) {
        if (isIFSAndCompetitionNotSetup(competitionId)) {
            return redirectString(COMPETITION_SETUP, competitionId);
        }
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        model.addAttribute("model", modelPopulator().populate(publicContent, readOnly));
        model.addAttribute("form", form.orElseGet(() -> formPopulator().populate(publicContent)));
        return TEMPLATE_FOLDER + "public-content-form";
    }

    private String readOnly(long competitionId, Model model, Optional<F> form) {
        return getPage(competitionId, model, form, true);
    }

    private String edit(long competitionId, Model model, Optional<F> form) {
        return getPage(competitionId, model, form, false);
    }

    private boolean isIFSAndCompetitionNotSetup(long competitionId) {
        return !competitionRestService.getCompetitionById(competitionId).getSuccess().isNonIfs() &&
                !competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId);
    }

    private String markAsComplete(long competitionId, Model model, F form, ValidationHandler validationHandler) {
        if (isIFSAndCompetitionNotSetup(competitionId)) {
            return redirectString(COMPETITION_SETUP, competitionId);
        }
        Supplier<String> successView = () -> getPage(competitionId, model, Optional.of(form), true);
        Supplier<String> failureView = () -> getPage(competitionId, model, Optional.of(form), false);
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        return validationHandler.performActionOrBindErrorsToField("", failureView, successView, () -> formSaver().markAsComplete(form, publicContent));
    }

    private String redirectString(String path, long competitionId) {
        return "redirect:" + path + competitionId;
    }

}
