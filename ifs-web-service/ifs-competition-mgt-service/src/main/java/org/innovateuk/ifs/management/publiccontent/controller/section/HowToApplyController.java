package org.innovateuk.ifs.management.publiccontent.controller.section;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.controller.AbstractContentGroupController;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.formpopulator.section.HowToApplyFormPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.section.HowToApplyViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.saver.section.HowToApplyFormSaver;
import org.innovateuk.ifs.management.publiccontent.form.section.HowToApplyForm;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.HowToApplyViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for how to apply of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/how-to-apply")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = HowToApplyController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class HowToApplyController extends AbstractContentGroupController<HowToApplyViewModel, HowToApplyForm> {

    @Autowired
    private HowToApplyFormPopulator howToApplyFormPopulator;

    @Autowired
    private HowToApplyViewModelPopulator howToApplyViewModelPopulator;

    @Autowired
    private HowToApplyFormSaver howToApplyFormSaver;

    @Override
    protected PublicContentViewModelPopulator<HowToApplyViewModel> modelPopulator() {
        return howToApplyViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<HowToApplyForm> formPopulator() {
        return howToApplyFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<HowToApplyForm> formSaver() {
        return howToApplyFormSaver;
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.HOW_TO_APPLY;
    }
}
