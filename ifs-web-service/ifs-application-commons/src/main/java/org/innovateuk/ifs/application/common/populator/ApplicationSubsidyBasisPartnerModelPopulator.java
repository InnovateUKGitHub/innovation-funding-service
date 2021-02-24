package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisPartnerViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static java.util.stream.Collectors.toList;

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