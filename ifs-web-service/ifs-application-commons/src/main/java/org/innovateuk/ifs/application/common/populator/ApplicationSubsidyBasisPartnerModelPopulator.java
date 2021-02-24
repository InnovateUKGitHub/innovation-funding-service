package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisPartnerViewModel;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSubsidyBasisPartnerModelPopulator {

    private final SectionService sectionService;
    private final ProcessRoleRestService processRoleRestService;
    private final OrganisationService organisationService;

    public ApplicationSubsidyBasisPartnerModelPopulator(SectionService sectionService,
                                                        ProcessRoleRestService processRoleRestService,
                                                        OrganisationService organisationService) {
        this.sectionService = sectionService;
        this.processRoleRestService = processRoleRestService;
        this.organisationService = organisationService;
    }

    public ApplicationSubsidyBasisPartnerViewModel populate() {
        return new ApplicationSubsidyBasisPartnerViewModel();
    }
}