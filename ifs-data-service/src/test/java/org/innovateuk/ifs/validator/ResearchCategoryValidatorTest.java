package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResearchCategoryValidatorTest {

    @InjectMocks
    private ResearchCategoryValidator validator;

    @Mock
    private ResearchCategoryRepository researchCategoryRepositoryMock;

    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        formInputResponse = newFormInputResponse()
                .withFormInputs(newFormInput()
                        .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                        .build())
                .build();

        List<ResearchCategory> researchCategories = newResearchCategory()
                .withName("Feasibility studies", "Industrial research", "Experimental development")
                .build(3);

        when(researchCategoryRepositoryMock.findAll()).thenReturn(researchCategories);

        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void validate() {
        formInputResponse.setValue("Feasibility studies");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_invalid() {
        formInputResponse.setValue("RESEARCH CATEGORY");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.category.invalidCategory", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_null() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.category.invalidCategory", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_wrongQuestionType() {
        formInputResponse.getFormInput().setType(FormInputType.ASSESSOR_SCORE);
        formInputResponse.setValue("Feasibility studies");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
