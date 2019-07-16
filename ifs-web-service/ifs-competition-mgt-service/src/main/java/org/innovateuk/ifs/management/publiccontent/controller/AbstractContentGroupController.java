package org.innovateuk.ifs.management.publiccontent.controller;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.management.publiccontent.form.ContentGroupForm;
import org.innovateuk.ifs.management.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fileUploadField;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * Abstract controller for all sections of public content with a repeating content group.
 * @param <M> the view model class
 * @param <F> the form class
 */
public abstract class AbstractContentGroupController<M extends AbstractPublicContentViewModel, F extends AbstractContentGroupForm> extends AbstractPublicContentSectionController<M, F> {

    @PostMapping(value = "/{competitionId}/edit", params = "uploadFile")
    public String saveAndUpload(Model model,
                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                () -> publicContentService.uploadFile(competitionId, getType(), form.getContentGroups()));
    }

    @PostMapping(value = "/{competitionId}/edit", params = "removeFile")
    public String saveAndRemove(Model model,
                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                () -> publicContentService.removeFile(form));
    }

    @GetMapping("/{competitionId}/edit/{contentGroupId}")
    public ResponseEntity<ByteArrayResource> getFileDetails(Model model,
                                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                           @PathVariable("contentGroupId") long contentGroupId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId)
                .getSuccess();

        if (!competition.isNonIfs() && !competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            throw new IllegalStateException("The competition 'Initial Details' section should be completed first.");
        }

        final ByteArrayResource resource = publicContentService.downloadAttachment(contentGroupId);
        FileEntryResource fileDetails = publicContentService.getFileDetails(contentGroupId);
        return getFileResponseEntity(resource, fileDetails);
    }

    private String saveAndFileAction(long competitionId, Model model, F form, ValidationHandler validationHandler,
                               Supplier<ServiceResult<Void>> action) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId)
                .getSuccess();

        if (!competition.isNonIfs() && !competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        Supplier<String> failureView = () -> getPage(competitionId, model, Optional.of(form), false);
        Supplier<String> successView = () -> getPage(competitionId, model, Optional.empty(), false);

        ServiceResult<Void> result = formSaver().save(form, publicContentService.getCompetitionById(competitionId));

        validationHandler.addAnyErrors(error(removeDuplicates(result.getErrors())));

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> fileResult = action.get();
            Optional<ContentGroupForm> groupWithAttachment = CollectionFunctions.simpleFindFirst(form.getContentGroups(),
                    contentGroupForm -> contentGroupForm.getAttachment() != null && !contentGroupForm.getAttachment().isEmpty());
            if (groupWithAttachment.isPresent()) {
                int index = form.getContentGroups().indexOf(groupWithAttachment.get());
                validationHandler.addAnyErrors(error(removeDuplicates(fileResult.getErrors())), fileUploadField(String.format("contentGroups[%s].attachment", index)), defaultConverters());
            }
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    protected abstract PublicContentSectionType getType();

}
