package org.innovateuk.ifs.publiccontent.modelpopulator.section;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.publiccontent.modelpopulator.AbstractPublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel;
import org.innovateuk.ifs.publiccontent.viewmodel.submodel.DateViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class DatesViewModelPopulator extends AbstractPublicContentViewModelPopulator<DatesViewModel> implements PublicContentViewModelPopulator<DatesViewModel> {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    protected DatesViewModel createInitial() {
        return new DatesViewModel();
    }

    @Override
    protected void populateSection(DatesViewModel model, PublicContentResource publicContentResource, PublicContentSectionResource section) {
        boolean nonIfs = competitionRestService.getCompetitionById(publicContentResource.getCompetitionId())
                .getSuccessObjectOrThrowException()
                .isNonIfs();

        List<MilestoneResource> milestones = milestoneRestService
                .getAllPublicMilestonesByCompetitionId(publicContentResource.getCompetitionId())
                .getSuccessObjectOrThrowException()
                .stream()
                .filter(milestoneResource -> isNonIFSCompAndIsNonIfsMilestoneOnly(nonIfs, milestoneResource))
                .collect(toList());


        List<DateViewModel> dates = new ArrayList<>();
        milestones.stream()
                .filter(milestoneResource -> filterOutNotificationsIfEmpty(milestoneResource))
                .forEach(milestoneResource -> dates.add(mapMilestoneToDateViewModel(milestoneResource)));

        if(model.isReadOnly()) {
            publicContentResource.getContentEvents().forEach(publicContentEventResource ->
                    dates.add(mapContentEventDateViewModel(publicContentEventResource)));
        }

        model.setPublicContentDates(dates);
    }

    private boolean isNonIFSCompAndIsNonIfsMilestoneOnly(boolean nonIfs, MilestoneResource milestoneResource) {
        if(!nonIfs) {
            return !milestoneResource.getType().isOnlyNonIfs();
        }
        return true;
    }

    private boolean filterOutNotificationsIfEmpty(MilestoneResource milestoneResource) {
        if (milestoneResource.getType().equals(MilestoneType.NOTIFICATIONS)) {
            return null != milestoneResource.getDate();
        }
        return true;
    }

    private DateViewModel mapContentEventDateViewModel(ContentEventResource contentEventResource) {
        DateViewModel dateViewModel = new DateViewModel();
        dateViewModel.setDateTime(contentEventResource.getDate());
        dateViewModel.setContent(contentEventResource.getContent());

        return dateViewModel;
    }

    private DateViewModel mapMilestoneToDateViewModel(MilestoneResource milestoneResource) {
        DateViewModel dateViewModel = new DateViewModel();
        dateViewModel.setDateTime(milestoneResource.getDate());
        switch (milestoneResource.getType()) {
            case OPEN_DATE:
                dateViewModel.setContent("Competition opens");
                break;
            case SUBMISSION_DATE:
                dateViewModel.setContent("Submission deadline, competition closed.");
                break;
            case REGISTRATION_DATE:
                dateViewModel.setContent("Registration closes");
                break;
            case NOTIFICATIONS:
                dateViewModel.setContent("Applicants notified");
                break;
        }

        return dateViewModel;
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }
}
