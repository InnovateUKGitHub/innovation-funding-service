package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.publiccontent.form.SearchInformationForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.SearchInformationFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.SearchInformationViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.SearchInformationFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.SearchInformationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for setup of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/search")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentSearchSectionController extends AbstractPublicContentSectionController<SearchInformationViewModel, SearchInformationForm> {

    @Autowired
    private SearchInformationFormPopulator searchInformationFormPopulator;

    @Autowired
    private SearchInformationViewModelPopulator searchInformationViewModelPopulator;

    @Autowired
    private SearchInformationFormSaver searchInformationFormSaver;

    @Override
    protected PublicContentViewModelPopulator<SearchInformationViewModel> modelPopulator() {
        return searchInformationViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<SearchInformationForm> formPopulator() {
        return searchInformationFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<SearchInformationForm> formSaver() {
        return searchInformationFormSaver;
    }

}
