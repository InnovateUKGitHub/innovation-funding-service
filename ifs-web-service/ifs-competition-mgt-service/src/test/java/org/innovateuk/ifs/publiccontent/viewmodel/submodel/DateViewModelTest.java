package org.innovateuk.ifs.publiccontent.viewmodel.submodel;

import org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

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
    public void getDateTimeTest() {
        String resultOne = viewModel.getDateTime();
        assertEquals("Unknown", resultOne);

        viewModel.setDateTime(LocalDateTime.of(2015,01,01,0,0));
        String resultTwo = viewModel.getDateTime();

        assertEquals("1 January 2015", resultTwo);

        viewModel.setDateTime(LocalDateTime.of(2015,01,25,0,0));

        String resultThree = viewModel.getDateTime();
        assertEquals("25 January 2015", resultThree);
    }
}
