package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;

import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AssessmentPanelServiceImplTest extends BaseServiceUnitTest<AssessmentPanelServiceImpl> {

    private static final long applicationId = 1L;
    private Application application;

    @Override
    protected AssessmentPanelServiceImpl supplyServiceUnderTest() {
        return new AssessmentPanelServiceImpl();
    }

    @Before
    public void setUp() {
        application = newApplication().withId(applicationId).build();
    }

    @Test
    public void assignApplicationsToPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<Void> result = service.assignApplicationToPanel(applicationId);
        assertTrue(result.isSuccess());
        assertTrue(application.isInAssessmentPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verifyNoMoreInteractions(applicationRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentReviewRepositoryMock
                .findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN))
                .thenReturn(emptyList());

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verify(assessmentReviewRepositoryMock).findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN);
        verifyNoMoreInteractions(applicationRepositoryMock, assessmentReviewRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel_existingReviews() throws Exception {
        List<AssessmentReview> assessmentReviews = newAssessmentReview().withTarget(application).withState(AssessmentReviewState.WITHDRAWN).build(2);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentReviewRepositoryMock
                .findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN))
                .thenReturn(assessmentReviews);

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentPanel());

        assessmentReviews.forEach(a -> assertEquals(State.WITHDRAWN, a.getActivityState().getBackingState()));

        verify(applicationRepositoryMock).findOne(applicationId);
        verifyNoMoreInteractions(applicationRepositoryMock);
    }
}
