package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApplicationAvailableAssessorsModelPopulator {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    public ApplicationAvailableAssessorsViewModel populateModel(CompetitionResource competition) {
        return new ApplicationAvailableAssessorsViewModel(getAvailableAssessors(competition));
    }

    private List<ApplicationAvailableAssessorsRowViewModel> getAvailableAssessors(CompetitionResource competition) {
        return competitionInviteRestService.getAvailableAssessors(competition.getId()).getSuccessObjectOrThrowException()
                .stream()
                .map(this::getRowViewModel)
                .collect(toList());
    }

    private ApplicationAvailableAssessorsRowViewModel getRowViewModel(AvailableAssessorResource availableAssessorResource) {
        return new ApplicationAvailableAssessorsRowViewModel("name", "skill areas", 10, 4, 3);
    }
}
