package org.innovateuk.ifs.application.forms.sections.yourfinances.populator;

import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesRowViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel.YourFinancesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class YourFinancesModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    public YourFinancesViewModel populate(long applicationId, long sectionId, UserResource user) {
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        return populate(applicationId, sectionId, processRole.getOrganisationId());
    }

    public YourFinancesViewModel populate(long applicationId, long sectionId, long organisationId) {
        SectionResource yourFinances = sectionRestService.getById(sectionId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        List<Long> completedSections = sectionStatusRestService.getCompletedSectionIds(applicationId, organisationId).getSuccess();

        List<YourFinancesRowViewModel> rows = yourFinances.getChildSections().stream().map(subSectionId -> {
            SectionResource subSection = sectionRestService.getById(subSectionId).getSuccess();
            return new YourFinancesRowViewModel(subSection.getName(), "", completedSections.contains(subSection.getId()));
        }).collect(toList());
        return new YourFinancesViewModel(applicationId, application.getName(), rows);
    }
}
