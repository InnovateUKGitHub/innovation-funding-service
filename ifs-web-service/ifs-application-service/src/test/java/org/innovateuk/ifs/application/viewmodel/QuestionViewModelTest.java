package org.innovateuk.ifs.application.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class QuestionViewModelTest {

    private QuestionViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new QuestionViewModel();
    }

    @Test
    public void testGetIsSection() {
        assertEquals(Boolean.FALSE, viewModel.getIsSection());
    }
}
