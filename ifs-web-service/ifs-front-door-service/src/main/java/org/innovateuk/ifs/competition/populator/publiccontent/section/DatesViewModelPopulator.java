package org.innovateuk.ifs.competition.populator.publiccontent.section;

import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentSectionViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.*;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.DatesViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.submodel.DateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Populates a public content eligibility view model.
 */

@Service
public class DatesViewModelPopulator extends AbstractPublicContentSectionViewModelPopulator<DatesViewModel> {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    protected DatesViewModel createInitial() {
        return new DatesViewModel();
    }

    @Override
    protected void populateSection(DatesViewModel model, PublicContentItemResource publicContentItemResource, PublicContentSectionResource section, Boolean nonIFS) {
        List<DateViewModel> publicContentDates = mapContentEventsToDatesViewModel(publicContentItemResource.getPublicContentResource().getContentEvents());
        publicContentDates.addAll(getMilestonesAsDatesViewModel(publicContentItemResource.getPublicContentResource().getCompetitionId(), publicContentItemResource));

        model.setPublicContentDates(publicContentDates);
    }

    private List<DateViewModel> getMilestonesAsDatesViewModel(Long competitionId, PublicContentItemResource publicContentItemResource) {
        List<MilestoneResource> milestones = milestoneRestService.getAllPublicMilestonesByCompetitionId(competitionId)
                .getSuccess();

        List<DateViewModel> milestonesMapped = mapMilestoneToDateViewModel(milestones, publicContentItemResource);
        return milestonesMapped;
    }

    private List<DateViewModel> mapMilestoneToDateViewModel(List<MilestoneResource> milestonesNeeded, PublicContentItemResource publicContentItemResource) {
        List<DateViewModel> publicContentDates = new ArrayList<>();

        milestonesNeeded.stream()
                .filter(milestoneResource -> milestoneResource.getDate() != null || nullDateAllowed(milestoneResource, publicContentItemResource))
                .forEach(milestoneResource -> {
                    DateViewModel publicContentDate = new DateViewModel();

                    publicContentDate.setDateTime(milestoneResource.getDate());
                    publicContentDate.setMustBeStrong(Boolean.FALSE);
                    switch (milestoneResource.getType()) {
                        case OPEN_DATE:
                            publicContentDate.setContent("Competition opens");
                            publicContentDate.setMustBeStrong(Boolean.TRUE);
                            break;
                        case REGISTRATION_DATE:
                            publicContentDate.setContent("Registration closes");
                            break;
                        case SUBMISSION_DATE:
                            if (publicContentItemResource.isAlwaysOpen() && milestoneResource.getDate() == null) {
                                publicContentDate.setContent("This is open-ended competition and applications can be submitted at any time.");
                                publicContentDate.setNullDateText("No submission deadline");
                            } else {
                                publicContentDate.setContent("Competition closes");
                            }
                            break;
                        case NOTIFICATIONS:
                            publicContentDate.setContent("Applicants notified");
                            break;
                        default:
                            // do nothing
                }
            publicContentDates.add(publicContentDate);
        });

        return publicContentDates;
    }

    private boolean nullDateAllowed(MilestoneResource milestoneResource, PublicContentItemResource publicContentItemResource) {
        return publicContentItemResource.isAlwaysOpen() && milestoneResource.getType() == MilestoneType.SUBMISSION_DATE;
    }

    private List<DateViewModel> mapContentEventsToDatesViewModel(List<ContentEventResource> contentEvents) {
        List<DateViewModel> publicContentDates = new ArrayList<>();

        contentEvents.stream()
                .filter(contentEventResource -> contentEventResource.getDate() != null)
                .forEach(contentEventResource -> {
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
