package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentEligibilityFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.EligibilityViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.EligibilityFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.EligibilityViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;

/**
 * Controller for setup of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/eligibility")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentEligibilitySectionController extends AbstractPublicContentSectionController<EligibilityViewModel, EligibilityForm> {

    @Autowired
    private PublicContentEligibilityFormPopulator publicContentEligibilityFormPopulator;

    @Autowired
    private EligibilityViewModelPopulator eligibilityViewModelPopulator;

    @Autowired
    private EligibilityFormSaver eligibilityFormSaver;

    @Override
    protected PublicContentViewModelPopulator<EligibilityViewModel> modelPopulator() {
        return eligibilityViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<EligibilityForm> formPopulator() {
        return publicContentEligibilityFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<EligibilityForm> formSaver() {
        return eligibilityFormSaver;
    }

    @RequestMapping(value = "/{competitionId}/edit", params = "uploadFile" ,method = RequestMethod.POST)
    public String saveAndUpload(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) EligibilityForm form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                (publicContentResource) -> publicContentService.uploadFile(publicContentResource, PublicContentSectionType.ELIGIBILITY, form.getContentGroups(), form.getUploadFile(), form.getAttachment()));
    }

    @RequestMapping(value = "/{competitionId}/edit", params = "removeFile" ,method = RequestMethod.POST)
    public String saveAndRemove(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                @ModelAttribute(FORM_ATTR_NAME) EligibilityForm form, BindingResult bindingResult, ValidationHandler validationHandler) {
        return saveAndFileAction(competitionId, model, form, validationHandler,
                (publicContentResource) -> publicContentService.removeFile(publicContentResource, PublicContentSectionType.ELIGIBILITY, form.getContentGroups(), form.getRemoveFile()));
    }

}
