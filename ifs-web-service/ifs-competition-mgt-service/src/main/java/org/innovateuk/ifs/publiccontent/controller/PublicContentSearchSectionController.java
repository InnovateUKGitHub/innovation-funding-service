package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.publiccontent.form.SearchForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentSearchFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentSearchModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentSearchFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.SearchViewModel;
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
public class PublicContentSearchSectionController extends AbstractPublicContentSectionController<SearchViewModel, SearchForm> {

    @Autowired
    private PublicContentSearchFormPopulator publicContentSearchFormPopulator;

    @Autowired
    private PublicContentSearchModelPopulator publicContentSearchModelPopulator;

    @Autowired
    private PublicContentSearchFormSaver publicContentSearchFormSaver;

    @Override
    protected PublicContentViewModelPopulator<SearchViewModel> modelPopulator() {
        return publicContentSearchModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<SearchForm> formPopulator() {
        return publicContentSearchFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<SearchForm> formSaver() {
        return publicContentSearchFormSaver;
    }

}
