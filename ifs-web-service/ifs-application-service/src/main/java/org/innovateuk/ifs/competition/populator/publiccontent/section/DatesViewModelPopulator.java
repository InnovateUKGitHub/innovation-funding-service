package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentSectionViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.DatesViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.submodel.DateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.MilestoneType.SUBMISSION_DATE;

/**
 * Populates a public content eligibility view model.
 */

@Service
public class DatesViewModelPopulator extends AbstractPublicContentSectionViewModelPopulator<DatesViewModel> {

    @Autowired
    private MilestoneService milestoneService;

    @Override
    protected DatesViewModel createInitial() {
        return new DatesViewModel();
    }

    @Override
    protected void populateSection(DatesViewModel model, PublicContentResource publicContentResource, PublicContentSectionResource section, Boolean nonIFS) {
        List<DateViewModel> publicContentDates = mapContentEventsToDatesViewModel(publicContentResource.getContentEvents());
        publicContentDates.addAll(getMilestonesAsDatesViewModel(nonIFS, publicContentResource.getCompetitionId()));

        model.setPublicContentDates(publicContentDates);
    }

    private List<DateViewModel> getMilestonesAsDatesViewModel(Boolean nonIFS, Long competitionId) {
        List<MilestoneResource> milestones = milestoneService.getAllPublicMilestonesByCompetitionId(competitionId);

        List<DateViewModel> mileStonesMapped = mapMilestoneToDateViewModel(milestones, nonIFS);
        if(nonIFS) {
            mileStonesMapped.add(addRegistrationCloseDateForNonIFSComp(
                    milestones.stream().filter(resource -> SUBMISSION_DATE.equals(resource.getType())).findFirst()));
        }

        return mileStonesMapped;
    }

    private DateViewModel addRegistrationCloseDateForNonIFSComp(Optional<MilestoneResource> competitionCloseDateMilestone) {
        DateViewModel registrationClosesDate = new DateViewModel();
        if(competitionCloseDateMilestone.isPresent()) {
            registrationClosesDate.setMustBeStrong(false);
            registrationClosesDate.setContent("Registration closes");
            registrationClosesDate.setDateTime(competitionCloseDateMilestone.get().getDate().minusDays(7));
        }

        return registrationClosesDate;
    }

    private List<DateViewModel> mapMilestoneToDateViewModel(List<MilestoneResource> milestonesNeeded, Boolean nonIfs) {
        List<DateViewModel> publicContentDates = new ArrayList<>();

        milestonesNeeded.forEach(milestoneResource -> {
            DateViewModel publicContentDate = new DateViewModel();

            publicContentDate.setDateTime(milestoneResource.getDate());
            publicContentDate.setMustBeStrong(Boolean.FALSE);
            switch (milestoneResource.getType()) {
                case OPEN_DATE:
                    publicContentDate.setContent("Competition opens");
                    publicContentDate.setMustBeStrong(Boolean.TRUE);
                    break;
                case SUBMISSION_DATE:
                    publicContentDate.setContent("Competition closes");
                    break;
                case RELEASE_FEEDBACK:
                    publicContentDate.setContent("Applicants notified");
                    break;
            }

            if(!(nonIfs && SUBMISSION_DATE.equals(milestoneResource.getType()))) {
                publicContentDates.add(publicContentDate);
            }
        });

        return publicContentDates;
    }

    private List<DateViewModel> mapContentEventsToDatesViewModel(List<ContentEventResource> contentEvents) {
        List<DateViewModel> publicContentDates = new ArrayList<>();

        contentEvents.forEach(contentEventResource -> {
            DateViewModel publicContentDate = new DateViewModel();
            publicContentDate.setDateTime(contentEventResource.getDate());
            publicContentDate.setContent(contentEventResource.getContent());
            publicContentDate.setMustBeStrong(Boolean.FALSE);

            publicContentDates.add(publicContentDate);
        });

        return publicContentDates;
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }
}
