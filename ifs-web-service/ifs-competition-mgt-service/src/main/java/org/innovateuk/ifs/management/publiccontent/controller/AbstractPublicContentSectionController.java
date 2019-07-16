package org.innovateuk.ifs.management.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.publiccontent.form.AbstractPublicContentForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.management.publiccontent.viewmodel.AbstractPublicContentViewModel;
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

import static org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType.DATES;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_ID_KEY;

/**
 * Abstract controller for all public content sections.
 */
public abstract class AbstractPublicContentSectionController<M extends AbstractPublicContentViewModel, F extends AbstractPublicContentForm> {

    protected static final String TEMPLATE_FOLDER = "competition/";
    protected static final String FORM_ATTR_NAME = "form";
    private static final String COMPETITION_SETUP_PATH = "/competition/setup/";
    private static final String COMPETITION_SETUP_PUBLIC_CONTENT_DATES_PATH = COMPETITION_SETUP_PATH + "public-content/dates/";
    private static final String REDIRECT = "redirect:";

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
            return redirectTo(COMPETITION_SETUP_PATH + competitionId);
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
            return redirectTo(COMPETITION_SETUP_PATH + competitionId);
        }
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        M populatedViewModel = modelPopulator().populate(publicContent, true);
        Supplier<String> successView = getSuccessView(competitionId, model, form, populatedViewModel);
        Supplier<String> failureView = () -> getPage(competitionId, model, Optional.of(form), false);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView, () -> formSaver().markAsComplete(form, publicContent));
    }

    private Supplier<String> getSuccessView(long competitionId, Model model, F form, M populatedViewModel) {
        return isInDatesSection(populatedViewModel) ?
                (() -> redirectTo(COMPETITION_SETUP_PUBLIC_CONTENT_DATES_PATH + competitionId)) :
                (() -> getPage(competitionId, model, Optional.of(form), true));
    }

    private boolean isInDatesSection(M populatedViewModel) {
        return DATES == populatedViewModel.getSection().getType();
    }

    private String redirectTo(String path) {
        return REDIRECT + path;
    }

}
