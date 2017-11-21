package org.innovateuk.ifs.publiccontent.controller.section;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.controller.AbstractContentGroupController;
import org.innovateuk.ifs.publiccontent.form.section.ScopeForm;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.section.ScopeFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.section.ScopeViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.section.ScopeFormSaver;
import org.innovateuk.ifs.publiccontent.viewmodel.section.ScopeViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the scope section of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content/scope")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ScopeController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class ScopeController extends AbstractContentGroupController<ScopeViewModel, ScopeForm> {

    @Autowired
    private ScopeFormPopulator scopeFormPopulator;

    @Autowired
    private ScopeViewModelPopulator scopeViewModelPopulator;

    @Autowired
    private ScopeFormSaver scopeFormSaver;

    @Override
    protected PublicContentViewModelPopulator<ScopeViewModel> modelPopulator() {
        return scopeViewModelPopulator;
    }

    @Override
    protected PublicContentFormPopulator<ScopeForm> formPopulator() {
        return scopeFormPopulator;
    }

    @Override
    protected PublicContentFormSaver<ScopeForm> formSaver() {
        return scopeFormSaver;
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SCOPE;
    }
}
