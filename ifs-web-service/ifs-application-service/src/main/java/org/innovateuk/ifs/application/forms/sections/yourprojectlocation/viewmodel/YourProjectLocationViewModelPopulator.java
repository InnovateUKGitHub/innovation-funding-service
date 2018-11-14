package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO DW - document this class
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

        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());

        boolean sectionMarkedAsComplete = completedSectionIds.contains(section.getSection().getId());

        boolean readOnly = sectionMarkedAsComplete || !section.getCompetition().isOpen() || !section.getApplication().isOpen();

        boolean open = section.getApplication().isOpen() && section.getCompetition().isOpen();

        return new YourProjectLocationViewModel(
                sectionMarkedAsComplete,
                readOnly,
                String.format("/application/%d/form/FINANCE", applicationId),
                section.getApplication().getName(),
                applicationId,
                sectionId,
                open);
    }
}
