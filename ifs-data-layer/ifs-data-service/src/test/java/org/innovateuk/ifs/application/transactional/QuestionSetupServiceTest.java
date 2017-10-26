package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.Application;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class QuestionSetupServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionSetupService service = new QuestionSetupServiceImpl();

    @Mock
    private SetupStatusService setupStatusService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Test
    public void testMarkQuestionInSetupAsCompleteFindOne() throws Exception {
        final Long questionId = 23L;
        final Long competitionId = 32L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource foundStatusResource = newSetupStatusResource()
                .withId(13L)
                .withClassName(Question.class.getName())
                .withClassPk(questionId)
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.FALSE).build();
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(Question.class.getName())
                .withClassPk(questionId)
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.TRUE).build();
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(Question.class.getName())
                .withClassPk(questionId)
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withCompleted(Boolean.TRUE).build();

        when(setupStatusService.findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(foundStatusResource));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markQuestionInSetupAsComplete(questionId, competitionId, parentSection);

        verify(setupStatusService, times(1)).findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId);
        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
    }

    @Test
    public void testMarkQuestionInSetupAsIncompleteCreateOne() throws Exception {
        final Long questionId = 23L;
        final Long competitionId = 32L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withClassName(Question.class.getName())
                .withClassPk(questionId)
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();
        savingStatus.setId(null);
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(Question.class.getName())
                .withClassPk(questionId)
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();
        final SetupStatusResource parentSectionStatus = newSetupStatusResource()
                .withId(12L)
                .withClassName(parentSection.getClass().getName())
                .withClassPk(parentSection.getId())
                .withParentId()
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();

        when(setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
        when(competitionSetupService.markSectionIncomplete(competitionId, parentSection)).thenReturn(serviceSuccess(parentSectionStatus));

        when(setupStatusService.findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId))
                .thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markQuestionInSetupAsIncomplete(questionId, competitionId, parentSection);

        verify(setupStatusService, times(1)).findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId);
        verify(competitionSetupService, times(1)).markSectionIncomplete(competitionId, parentSection);
        verify(setupStatusService, times(1)).findSetupStatusAndTarget(Question.class.getName(), questionId, Competition.class.getName(), competitionId);
        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
    }

    @Test
    public void testGetQuestionStatuses() throws Exception {
        final Long questionId = 23L;
        final Long competitionId = 32L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final List<SetupStatusResource> foundStatuses = newSetupStatusResource()
                .withId(13L)
                .withClassName(Question.class.getName(), Application.class.getName())
                .withClassPk(questionId, 14L)
                .withTargetClassName(Competition.class.getName(), Competition.class.getName())
                .withTargetId(competitionId, competitionId)
                .withParentId(12L, 12L)
                .withCompleted(Boolean.FALSE, Boolean.TRUE).build(2);
        final SetupStatusResource parentSectionStatus = newSetupStatusResource()
                .withId(12L)
                .withClassName(parentSection.getClass().getName())
                .withClassPk(parentSection.getId())
                .withParentId()
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();

        when(setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(parentSectionStatus));

        when(setupStatusService.findByTargetClassNameAndTargetIdAndParentId(Competition.class.getName(), competitionId, parentSectionStatus.getId()))
                .thenReturn(serviceSuccess(foundStatuses));

        Map<Long, Boolean> result = service.getQuestionStatuses(competitionId, parentSection).getSuccessObjectOrThrowException();

        verify(setupStatusService, times(1)).findByTargetClassNameAndTargetIdAndParentId(Competition.class.getName(), competitionId, parentSectionStatus.getId());
        verify(setupStatusService, times(1)).findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId);

        assertEquals(1, result.size());
        assertEquals(false, result.get(questionId));
    }
}
