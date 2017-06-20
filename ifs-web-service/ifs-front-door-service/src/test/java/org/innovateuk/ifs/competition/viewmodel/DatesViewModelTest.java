package org.innovateuk.ifs.competition.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.DatesViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.submodel.DateViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Testing {@link DatesViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class DatesViewModelTest {

    @InjectMocks
    private DatesViewModel viewModel;

    private Map<Long, FileEntryResource> fileEntries;
    public List<ContentGroupResource> contentGroups;

    @Before
    public void setup() {
        viewModel = new DatesViewModel();
    }

    @Test
    public void getSortedEventsTest() {
        DateViewModel closeDate = new DateViewModel(ZonedDateTime.now().plusDays(2), "Close", FALSE);
        DateViewModel openDate = new DateViewModel(ZonedDateTime.now().minusDays(1), "Open", TRUE);
        DateViewModel nowDate = new DateViewModel(ZonedDateTime.now(), "Now", FALSE);

        viewModel.setPublicContentDates(asList(closeDate, openDate, nowDate));
        List<DateViewModel> resultOne = viewModel.getSortedEvents();

        assertEquals(openDate, resultOne.get(0));
        assertEquals(nowDate, resultOne.get(1));
        assertEquals(closeDate, resultOne.get(2));

        viewModel.setPublicContentDates(asList(closeDate, nowDate, openDate));
        List<DateViewModel> resultTwo = viewModel.getSortedEvents();

        assertEquals(openDate, resultTwo.get(0));
        assertEquals(nowDate, resultTwo.get(1));
        assertEquals(closeDate, resultTwo.get(2));
    }
}
