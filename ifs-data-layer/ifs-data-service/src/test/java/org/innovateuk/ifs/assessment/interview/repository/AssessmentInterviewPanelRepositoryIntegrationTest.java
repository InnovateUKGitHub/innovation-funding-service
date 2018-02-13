package org.innovateuk.ifs.assessment.interview.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class AssessmentInterviewPanelRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentInterviewPanelRepository> {

    @Autowired
    @Override
    protected void setRepository(AssessmentInterviewPanelRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByTargetCompetitionIdAndActivityStateState() {
        long competitionId=1;
        State state = AssessmentInterviewPanelState.CREATED.getBackingState();
        Pageable pageable =  new PageRequest(0, 20);

        repository.findByTargetCompetitionIdAndActivityStateState(competitionId, state, pageable);
    }
}