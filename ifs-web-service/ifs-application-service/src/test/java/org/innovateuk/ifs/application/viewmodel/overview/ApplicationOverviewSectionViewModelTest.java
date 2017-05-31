package org.innovateuk.ifs.application.viewmodel.overview;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Testing {@link ApplicationOverviewSectionViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationOverviewSectionViewModelTest {

    private ApplicationOverviewSectionViewModel viewModel;

    @Before
    public void setup() {
        SortedMap<Long, SectionResource> sections = mock(SortedMap.class);
        Map<Long, List<SectionResource>> subSections = asMap(1L, asList(), 2L, asList(2L, 3L));
        Map<Long, List<QuestionResource>> sectionQuestions = asMap();
        Map<Long, AssignButtonsViewModel> assignButtonViewModels = asMap();
        List<SectionResource> financeSections = asList();
        Boolean hasFinanceSection = Boolean.TRUE;
        Long financeSectionId = 123L;

        viewModel = new ApplicationOverviewSectionViewModel(sections, subSections, sectionQuestions, financeSections, hasFinanceSection, financeSectionId, assignButtonViewModels);
    }

    @Test
    public void hasSubSectionTest() {
        assertFalse(viewModel.hasSubSection(1L));
        assertTrue(viewModel.hasSubSection(2L));
    }
}
