package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.AvailableAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Invite assessors 'Find' view.
 */
@Component
public class InviteAssessorsFindModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsFindViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Override
    public InviteAssessorsFindViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsFindViewModel model = super.populateModel(competition);
        model.setAssessors(getAssessors(competition));
        return model;
    }

    private List<AvailableAssessorRowViewModel> getAssessors(CompetitionResource competition) {
        return competitionInviteRestService.getAvailableAssessors(competition.getId()).getSuccessObjectOrThrowException()
                .stream()
                .map(this::getRowViewModel)
                .collect(toList());
    }

    private AvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource availableAssessorResource) {
        String name = Stream.of(availableAssessorResource.getFirstName(), availableAssessorResource.getLastName()).filter(StringUtils::isNotBlank).collect(joining(" "));
        String innovationArea = availableAssessorResource.getInnovationArea().getName();
        return new AvailableAssessorRowViewModel(name, innovationArea, availableAssessorResource.isCompliant(), availableAssessorResource.getBusinessType(), availableAssessorResource.isAdded());
    }

    @Override
    protected InviteAssessorsFindViewModel createModel() {
        return new InviteAssessorsFindViewModel();
    }
}
