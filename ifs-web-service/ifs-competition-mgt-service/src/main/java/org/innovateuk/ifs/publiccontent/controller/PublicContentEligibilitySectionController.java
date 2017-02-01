package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
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
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for setup of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/eligibility")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentEligibilitySectionController extends AbstractContentGroupController<EligibilityViewModel, EligibilityForm> {

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

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
