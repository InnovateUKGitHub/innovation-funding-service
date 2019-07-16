package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.ProjectForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectFormPopulatorTest {

    @InjectMocks
    private ProjectFormPopulator populator;

    @Mock
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Mock
    private QuestionRestService questionRestService;

    private Long questionId = 7890L;
    private Long questionNotFoundId = 12904L;
    private QuestionResource questionResource = newQuestionResource().withId(questionId).build();
    private CompetitionResource competitionResource;


    @Test
    public void testPopulateFormWithoutErrors() {
        CompetitionSetupQuestionResource resource = new CompetitionSetupQuestionResource();
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(questionResource));
        when(questionSetupCompetitionRestService.getByQuestionId(questionId)).thenReturn(restSuccess(resource));

        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionId));

        assertTrue(result instanceof ProjectForm);
        ProjectForm form = (ProjectForm) result;
        assertEquals(form.getQuestion(), resource);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithErrors() {
        when(questionSetupCompetitionRestService.getByQuestionId(questionNotFoundId)).thenThrow(new ObjectNotFoundException());
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.of(questionNotFoundId));
        assertNull(result);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testPopulateFormWithNoObjectIdErrors() {
        CompetitionSetupForm result = populator.populateForm(competitionResource, Optional.empty());
        assertNull(result);
    }

}
