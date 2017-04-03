package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;

/**
 * Abstract controller for all public content sections.
 */
public abstract class AbstractPublicContentSectionController<M extends AbstractPublicContentViewModel, F extends AbstractPublicContentForm> {

    protected static final String TEMPLATE_FOLDER = "competition/";
    protected static final String FORM_ATTR_NAME = "form";

    @Autowired
    protected PublicContentService publicContentService;

    protected abstract PublicContentViewModelPopulator<M> modelPopulator();
    protected abstract PublicContentFormPopulator<F> formPopulator();
    protected abstract PublicContentFormSaver<F> formSaver();


    @GetMapping("/{competitionId}")
    public String readOnly(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        return readOnly(competitionId, model, Optional.empty());
    }

    @GetMapping("/{competitionId}/edit")
    public String edit(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        return edit(competitionId, model, Optional.empty());
    }

    @PostMapping(value = "/{competitionId}/edit")
    public String markAsComplete(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                       @Valid @ModelAttribute(FORM_ATTR_NAME) F form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return markAsComplete(competitionId, model, form, validationHandler);
    }

    protected String readOnly(Long competitionId, Model model, Optional<F> form) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        return getPage(publicContent, model, form, true);
    }

    protected String edit(Long competitionId, Model model, Optional<F> form) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        return getPage(publicContent, model, form, false);
    }

    protected String getPage(PublicContentResource publicContent, Model model, Optional<F> form, boolean readOnly) {
        model.addAttribute("model", modelPopulator().populate(publicContent, readOnly));
        if(form.isPresent()) {
            model.addAttribute("form", form.get());
        } else {
            model.addAttribute("form", formPopulator().populate(publicContent));
        }
        return TEMPLATE_FOLDER + "public-content-form";
    }

    protected String markAsComplete(Long competitionId, Model model, F form, ValidationHandler validationHandler) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        Supplier<String> successView = () -> "redirect:/competition/setup/public-content/" + competitionId;
        Supplier<String> failureView = () -> getPage(publicContent, model, Optional.of(form), false);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView, () -> formSaver().markAsComplete(form, publicContent));
    }

}
