package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationOverviewSectionViewModelTest {

    @Test
    public void isHasSubSection() {
        Map<Long, List<SectionResource>> subSections = asMap(1L, emptyList(), 2L, asList(2L, 3L));

        ApplicationOverviewSectionViewModel viewModel = new ApplicationOverviewSectionViewModel(null, subSections,
                null, false, null, null);

        assertFalse(viewModel.hasSubSection(1L));
        assertTrue(viewModel.hasSubSection(2L));
        assertFalse(viewModel.hasSubSection(3L));
    }
}
