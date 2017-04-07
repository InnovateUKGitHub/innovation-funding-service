package org.innovateuk.ifs.publiccontent.viewmodel;

import org.innovateuk.ifs.publiccontent.viewmodel.submodel.DateViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DatesViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class DatesViewModelTest {
    private DatesViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new DatesViewModel();

        DateViewModel dateViewModel1 = new DateViewModel();
        dateViewModel1.setDateTime(ZonedDateTime.now());
        dateViewModel1.setContent("Date 1");

        DateViewModel dateViewModel2 = new DateViewModel();
        dateViewModel2.setDateTime(ZonedDateTime.now().minusDays(2));
        dateViewModel2.setContent("Date 2");

        DateViewModel dateViewModel3 = new DateViewModel();
        dateViewModel3.setDateTime(ZonedDateTime.now().plusDays(3));
        dateViewModel3.setContent("Date 3");

        viewModel.setPublicContentDates(asList(dateViewModel1, dateViewModel2, dateViewModel3));
    }

    @Test
    public void getSortedEventsTest() {
        List<DateViewModel> result = viewModel.getSortedEvents();

        assertEquals("Date 2", result.get(0).getContent());
        assertEquals("Date 1", result.get(1).getContent());
        assertEquals("Date 3", result.get(2).getContent());
    }

    @Test
    public void getSortedEventsWithNullTest() {
        DateViewModel dateViewModel1 = new DateViewModel();
        dateViewModel1.setDateTime(ZonedDateTime.now());
        dateViewModel1.setContent("Date 1");

        DateViewModel dateViewModel2 = new DateViewModel();
        dateViewModel2.setDateTime(ZonedDateTime.now().minusDays(2));
        dateViewModel2.setContent("Date 2");

        DateViewModel dateViewModel3 = new DateViewModel();
        dateViewModel3.setDateTime(null);
        dateViewModel3.setContent("Date 3");

        DateViewModel dateViewModel4 = new DateViewModel();
        dateViewModel4.setDateTime(ZonedDateTime.now().plusDays(3));
        dateViewModel4.setContent("Date 4");

        viewModel.setPublicContentDates(asList(dateViewModel1, dateViewModel2, dateViewModel3, dateViewModel4));

        List<DateViewModel> result = viewModel.getSortedEvents();

        assertEquals("Date 2", result.get(0).getContent());
        assertEquals("Date 1", result.get(1).getContent());
        assertEquals("Date 4", result.get(2).getContent());
        assertEquals("Date 3", result.get(3).getContent());
    }
}
