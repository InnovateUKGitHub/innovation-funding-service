package org.innovateuk.ifs.publiccontent.controller.section;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.controller.AbstractContentGroupController;
import org.innovateuk.ifs.publiccontent.form.section.SupportingInformationForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.section.SupportingInformationFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.section.SupportingInformationViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.section.SupportingInformationFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.section.SupportingInformationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the supporting information of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/supporting-information")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SupportingInformationController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class SupportingInformationController extends AbstractContentGroupController<SupportingInformationViewModel, SupportingInformationForm> {

    @Autowired
    private SupportingInformationFormPopulator supportingInformationFormPopulator;

    @Autowired
    private SupportingInformationViewModelPopulator supportingInformationViewModelPopulator;

    @Autowired
    private SupportingInformationFormSaver supportingInformationFormSaver;

    @Override
    protected PublicContentViewModelPopulator<SupportingInformationViewModel> modelPopulator() {
        return supportingInformationViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<SupportingInformationForm> formPopulator() {
        return supportingInformationFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<SupportingInformationForm> formSaver() {
        return supportingInformationFormSaver;
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUPPORTING_INFORMATION;
    }
}
