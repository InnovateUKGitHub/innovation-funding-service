package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.repository.MultipleChoiceOptionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.MultipleChoiceOptionBuilder.newMultipleChoiceOption;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequiredMultipleChoiceValidatorTest {

    @InjectMocks
	private RequiredMultipleChoiceValidator validator;

    @Mock
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        formInputResponse = newFormInputResponse().withFormInputs(newFormInput().build()).build();
        bindingResult = ValidatorTestUtil.getBindingResult(formInputResponse);
    }

    @Test
    public void valid() {
        MultipleChoiceOption multipleChoiceOption =newMultipleChoiceOption()
                .withId(1L)
                .withText("Yes")
                .withFormInput(formInputResponse.getFormInput()).build();
        formInputResponse.setMultipleChoiceOption(multipleChoiceOption);
        when(multipleChoiceOptionRepository.findById(1L)).thenReturn(Optional.of(multipleChoiceOption));
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void missingValue() {
        formInputResponse.setMultipleChoiceOption(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void missingOption() {
        MultipleChoiceOption multipleChoiceOption =newMultipleChoiceOption()
                .withId(5L)
                .withText("No")
                .withFormInput(formInputResponse.getFormInput()).build();
        formInputResponse.setMultipleChoiceOption(multipleChoiceOption);
        when(multipleChoiceOptionRepository.findById(5L)).thenReturn(Optional.empty());
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
}
