package org.innovateuk.ifs.management.publiccontent.controller.section;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.publiccontent.controller.AbstractPublicContentSectionController;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.formpopulator.section.SearchInformationFormPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.section.SearchInformationViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.saver.section.SearchInformationFormSaver;
import org.innovateuk.ifs.management.publiccontent.form.section.SearchInformationForm;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.SearchInformationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for search section of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/search")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = PublicContentSearchSectionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
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
