package org.innovateuk.ifs.publiccontent.viewmodel.submodel;

import org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DatesViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class DateViewModelTest {
    private DateViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new DateViewModel();
    }

    @Test
    public void getDateTimeFormattedTest() {
        String resultOne = viewModel.getDateTimeFormatted();
        assertEquals("Unknown", resultOne);

        viewModel.setDateTime(ZonedDateTime.of(2015,1,1,0,0,0,0, ZoneId.systemDefault()));
        String resultTwo = viewModel.getDateTimeFormatted();

        assertEquals("1 January 2015", resultTwo);

        viewModel.setDateTime(ZonedDateTime.of(2015,1,25,0,0,0,0, ZoneId.systemDefault()));

        String resultThree = viewModel.getDateTimeFormatted();
        assertEquals("25 January 2015", resultThree);
    }
}
