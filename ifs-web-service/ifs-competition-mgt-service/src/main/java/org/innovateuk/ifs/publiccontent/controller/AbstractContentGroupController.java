package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;

public abstract class AbstractContentGroupController<M extends AbstractPublicContentViewModel, F extends AbstractContentGroupForm> extends AbstractPublicContentSectionController<M, F> {

    @RequestMapping(value = "/{competitionId}/edit", params = "uploadFile" ,method = RequestMethod.POST)
    public String saveAndUpload(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                (publicContentResource) -> publicContentService.uploadFile(publicContentResource, PublicContentSectionType.ELIGIBILITY, form.getContentGroups(), form.getUploadFile(), form.getAttachment()));
    }

    @RequestMapping(value = "/{competitionId}/edit", params = "removeFile" ,method = RequestMethod.POST)
    public String saveAndRemove(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) F form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                (publicContentResource) -> publicContentService.removeFile(publicContentResource, PublicContentSectionType.ELIGIBILITY, form.getContentGroups(), form.getRemoveFile()));
    }

}
