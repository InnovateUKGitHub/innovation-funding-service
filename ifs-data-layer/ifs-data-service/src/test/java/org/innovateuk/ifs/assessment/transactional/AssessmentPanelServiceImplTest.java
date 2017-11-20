package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AssessmentPanelServiceImplTest extends BaseServiceUnitTest<AssessmentPanelServiceImpl> {

    long applicationId = 1L;
    private Application application;

    @Override
    protected AssessmentPanelServiceImpl supplyServiceUnderTest() {
        return new AssessmentPanelServiceImpl();
    }

    @Before
    public void setUp() {
        application = newApplication().build();
    }

    @Test
    public void assignApplicationsToPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<Void> result = service.assignApplicationToPanel(applicationId);
        assertTrue(result.isSuccess());
        assertTrue(application.isInAssessmentPanel());
    }

    @Test
    public void unAssignApplicationsFromPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<Void> result = service.unAssignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentPanel());
    }
}
