package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.form.resource.FormInputResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
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

    @Test
    public void testGetHasCurrentQuestionFormInputs() {
        assertEquals(Boolean.FALSE, viewModel.getHasCurrentQuestionFormInputs());

        Map<Long, List<FormInputResource>> questionFormInputs = asMap(23L, newFormInputResource().build());
        viewModel.setQuestionFormInputs(questionFormInputs);

        assertEquals(Boolean.TRUE, viewModel.getHasCurrentQuestionFormInputs());
    }
}
