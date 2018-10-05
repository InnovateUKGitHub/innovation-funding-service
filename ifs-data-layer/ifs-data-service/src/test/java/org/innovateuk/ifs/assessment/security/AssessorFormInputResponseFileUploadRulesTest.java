package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessorFormInputResponseFileUploadRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorFormInputResponseFileUploadRules fileUploadRules;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    private static final long formInputId = 123L;
    private static final long applicationId = 456L;
    private static final long processRoleId = 789L;

    @Test
    public void assessorCanDownloadFilesForApplicationTheyAreAssessing() {
        UserResource assessor = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR))
                .build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        ProcessRole assessorProcessRole = newProcessRole()
                .withId(processRoleId)
                .build();
        Assessment assessment = newAssessment()
                .withProcessState(AssessmentState.ACCEPTED)
                .build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId))
                .thenReturn(assessorProcessRole);
        when(assessmentRepositoryMock.findOneByParticipantId(processRoleId)).thenReturn(assessment);

        assertTrue(fileUploadRules.assessorCanDownloadFileForApplicationTheyAreAssessing(file, assessor));

        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId);
        verify(assessmentRepositoryMock).findOneByParticipantId(processRoleId);
    }

    @Test
    public void assessorCannotDownloadFilesForApplicationAssessmentTheyHaventAccepted() {
        UserResource assessor = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR))
                .build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        ProcessRole assessorProcessRole = newProcessRole()
                .withId(processRoleId)
                .build();
        Assessment assessment = newAssessment()
                .withProcessState(AssessmentState.PENDING)
                .build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId))
                .thenReturn(assessorProcessRole);
        when(assessmentRepositoryMock.findOneByParticipantId(processRoleId)).thenReturn(assessment);

        assertFalse(fileUploadRules.assessorCanDownloadFileForApplicationTheyAreAssessing(file, assessor));

        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId);
        verify(assessmentRepositoryMock).findOneByParticipantId(processRoleId);
    }

    @Test
    public void panelAssessorCanDownloadFilesForApplicationTheyAreAssessing() {
        UserResource assessor = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR))
                .build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        ProcessRole assessorProcessRole = newProcessRole().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.PANEL_ASSESSOR, applicationId))
                .thenReturn(assessorProcessRole);

        assertTrue(fileUploadRules.assessorCanDownloadFileForApplicationTheyAreAssessing(file, assessor));

        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.PANEL_ASSESSOR, applicationId);
    }

    @Test
    public void interviewAssessorCanDownloadFilesForApplicationTheyAreAssessing() {
        UserResource assessor = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR))
                .build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        ProcessRole assessorProcessRole = newProcessRole().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.INTERVIEW_ASSESSOR, applicationId))
                .thenReturn(assessorProcessRole);

        assertTrue(fileUploadRules.assessorCanDownloadFileForApplicationTheyAreAssessing(file, assessor));

        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.INTERVIEW_ASSESSOR, applicationId);
    }

    @Test
    public void assessorCanNotDownloadFilesForApplicationTheyAreNotAssessing() {
        UserResource assessor = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR))
                .build();
        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResponseFileEntryResource file = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);

        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId))
                .thenReturn(null);

        assertFalse(fileUploadRules.assessorCanDownloadFileForApplicationTheyAreAssessing(file, assessor));

        verify(processRoleRepositoryMock).findByUserIdAndRoleAndApplicationId(assessor.getId(), Role.ASSESSOR, applicationId);
    }
}
