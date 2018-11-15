package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A populator to build a YourProjectLocationViewModel
 */
@Component
public class YourProjectLocationViewModelPopulator {

    private ApplicantRestService applicantRestService;
    private SectionService sectionService;

    public YourProjectLocationViewModelPopulator(
            ApplicantRestService applicantRestService,
            SectionService sectionService) {

        this.applicantRestService = applicantRestService;
        this.sectionService = sectionService;
    }

    public YourProjectLocationViewModel populate(long userId, long applicationId, long sectionId) {

        ApplicantSectionResource section = applicantRestService.getSection(userId, applicationId, sectionId);

        long organisationId = section.getCurrentApplicant().getOrganisation().getId();

        List<Long> completedSectionIds = sectionService.getCompleted(applicationId, organisationId);

        boolean sectionMarkedAsComplete = completedSectionIds.contains(sectionId);

        boolean open = section.getApplication().isOpen() && section.getCompetition().isOpen();

        return new YourProjectLocationViewModel(
                sectionMarkedAsComplete,
                String.format("/application/%d/form/FINANCE", applicationId),
                section.getApplication().getName(),
                applicationId,
                sectionId,
                open);
    }
}
