package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Abstract controller for all sections of public content with a repeating content group.
 * @param <M> the view model class
 * @param <F> the form class
 */
public abstract class AbstractContentGroupController<M extends AbstractPublicContentViewModel, F extends AbstractContentGroupForm> extends AbstractPublicContentSectionController<M, F> {

    @PostMapping(value = "/{competitionId}/edit", params = "uploadFile")
    public String saveAndUpload(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                () -> publicContentService.uploadFile(competitionId, getType(), form.getContentGroups()));
    }

    @PostMapping(value = "/{competitionId}/edit", params = "removeFile")
    public String saveAndRemove(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                () -> publicContentService.removeFile(form));
    }

    @GetMapping("/{competitionId}/edit/{contentGroupId}")
    public ResponseEntity<ByteArrayResource> getFileDetails(Model model,
                                                           @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                           @PathVariable("contentGroupId") Long contentGroupId) {
        final ByteArrayResource resource = publicContentService.downloadAttachment(contentGroupId);
        FileEntryResource fileDetails = publicContentService.getFileDetails(contentGroupId);
        return getFileResponseEntity(resource, fileDetails);
    }

    protected String saveAndFileAction(Long competitionId, Model model, F form, ValidationHandler validationHandler, Supplier<ServiceResult<Void>> action) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);

        Supplier<String> failureView = () -> getPage(publicContent, model, Optional.of(form), false);
        //Pass in the public content resource after saving for success view.
        Supplier<String> successView = () -> getPage(publicContentService.getCompetitionById(competitionId), model, Optional.empty(), false);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> formSaver().save(form, publicContent).andOnSuccess(action));

    }

    protected abstract PublicContentSectionType getType();

}
