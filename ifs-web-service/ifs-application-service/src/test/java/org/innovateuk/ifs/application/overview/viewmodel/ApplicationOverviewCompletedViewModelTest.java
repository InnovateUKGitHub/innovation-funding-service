package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;
import java.util.concurrent.Future;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ApplicationOverviewSectionViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationOverviewCompletedViewModelTest {

    private ApplicationOverviewCompletedViewModel viewModel;

    @Before
    public void setup() throws Exception {
        Set<Long> sectionsMarkedAsComplete = asSet(1L, 2L);
        Boolean allQuestionsCompleted = Boolean.FALSE;
        Future<Set<Long>> markedAsComplete = mock(Future.class);
        when(markedAsComplete.get()).thenReturn(sectionsMarkedAsComplete);

        Set<Long> completedSections = asSet(1L, 2L);
        Boolean userFinanceSectionCompleted = Boolean.FALSE;

        viewModel = new ApplicationOverviewCompletedViewModel(sectionsMarkedAsComplete, allQuestionsCompleted, markedAsComplete, userFinanceSectionCompleted);
        viewModel.setCompletedSections(completedSections);
    }

    @Test
    public void completedOrMarkedAsCompleteTest() throws Exception{
        QuestionResource questionResource = newQuestionResource()
                .with(questionResource1 -> {
                    questionResource1.setId(1L);
                    questionResource1.setMarkAsCompletedEnabled(Boolean.TRUE);
                })
        .build();
        SectionResource sectionResource = newSectionResource().withId(54L).build();
        assertTrue(viewModel.completedOrMarkedAsComplete(questionResource, sectionResource));

        questionResource = newQuestionResource()
                .with(questionResource1 -> {
                    questionResource1.setId(23L);
                    questionResource1.setMarkAsCompletedEnabled(Boolean.TRUE);
                })
                .build();
        assertFalse(viewModel.completedOrMarkedAsComplete(questionResource, sectionResource));

        questionResource = newQuestionResource()
                .with(questionResource1 -> {
                    questionResource1.setId(3L);
                    questionResource1.setMarkAsCompletedEnabled(Boolean.FALSE);
                })
                .build();
        assertFalse(viewModel.completedOrMarkedAsComplete(questionResource, sectionResource));

        sectionResource = newSectionResource().withId(1L).build();
        assertTrue(viewModel.completedOrMarkedAsComplete(questionResource, sectionResource));
    }
}
