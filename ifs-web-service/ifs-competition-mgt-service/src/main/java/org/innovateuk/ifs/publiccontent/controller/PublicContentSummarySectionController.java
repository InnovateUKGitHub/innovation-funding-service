package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.publiccontent.form.SummaryForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.SummaryFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.SummaryViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.SummaryFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.SummaryViewModel;
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
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentSummarySectionController extends AbstractPublicContentSectionController<SummaryViewModel, SummaryForm> {

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

}
