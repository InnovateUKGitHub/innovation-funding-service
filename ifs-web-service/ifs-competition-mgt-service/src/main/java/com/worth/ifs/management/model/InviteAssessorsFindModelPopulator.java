package com.worth.ifs.management.model;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.resource.AvailableAssessorResource;
import com.worth.ifs.management.viewmodel.AssessorViewModel;
import com.worth.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.apache.commons.lang3.StringUtils;
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

    private List<AssessorViewModel> getAssessors(CompetitionResource competition) {
        return competitionInviteRestService.getAvailableAssessors(competition.getId()).getSuccessObjectOrThrowException()
                .stream()
                .map(this::getAssessorViewModel)
                .collect(toList());
    }

    private AssessorViewModel getAssessorViewModel(AvailableAssessorResource assessor) {
        String name = Stream.of(assessor.getFirstName(), assessor.getLastName()).filter(StringUtils::isNotBlank).collect(joining(" "));
        String innovationArea = assessor.getInnovationArea().getName();
        return new AssessorViewModel(assessor.getUserId(), name, assessor.getEmail(), assessor.getBusinessType(), innovationArea, assessor.isCompliant(), assessor.isAdded());
    }

    @Override
    protected InviteAssessorsFindViewModel createModel() {
        return new InviteAssessorsFindViewModel();
    }
}