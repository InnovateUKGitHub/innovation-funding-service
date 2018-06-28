package org.innovateuk.ifs.application.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class NavigationViewModelTest {

    private NavigationViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new NavigationViewModel();
    }

    @Test
    public void testGetHasNavigation() {
        viewModel.setNextText("Next one please");
        viewModel.setNextUrl("http://NextURL");

        assertEquals(Boolean.TRUE, viewModel.getHasNavigation());
    }

    @Test
    public void testGetHasNavigationFalse() {
        assertEquals(Boolean.FALSE, viewModel.getHasNavigation());
    }
}
