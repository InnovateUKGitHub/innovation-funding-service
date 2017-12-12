package org.innovateuk.ifs.publiccontent.controller.section;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.controller.AbstractContentGroupController;
import org.innovateuk.ifs.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.section.SummaryFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.section.SummaryViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.section.SummaryFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.section.SummaryViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to the public content Summary section.
 * The Summary section data is the most basic information that will be displayed about a competition in its public listing.
 */
@Controller
@RequestMapping("/competition/setup/public-content/summary")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = PublicContentSummarySectionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentSummarySectionController extends AbstractContentGroupController<SummaryViewModel, SummaryForm> {

    @Autowired
    private SummaryFormPopulator summaryFormPopulator;

    @Autowired
    private SummaryViewModelPopulator summaryViewModelPopulator;

    @Autowired
    private SummaryFormSaver summaryFormSaver;

    @Override
    protected PublicContentViewModelPopulator<SummaryViewModel> modelPopulator() {
        return summaryViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<SummaryForm> formPopulator() {
        return summaryFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<SummaryForm> formSaver() {
        return summaryFormSaver;
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
