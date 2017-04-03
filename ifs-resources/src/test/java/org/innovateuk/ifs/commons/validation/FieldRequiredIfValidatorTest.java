package org.innovateuk.ifs.commons.validation;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class FieldRequiredIfValidatorTest {

    private LocalValidatorFactoryBean localValidatorFactory;


    private TestController controller = new TestController();

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller).build();

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void isValid_stringFieldIsRequiredAndNotEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "true")
                .param("pleaseGiveFurtherDetails", " some further details "))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_stringFieldIsRequiredAndEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "true")
                .param("pleaseGiveFurtherDetails", ""))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "pleaseGiveFurtherDetails"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_stringFieldIsRequiredAndWhitespace() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "true")
                .param("pleaseGiveFurtherDetails", "  "))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "pleaseGiveFurtherDetails"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_stringFieldIsRequiredAndNull() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "pleaseGiveFurtherDetails"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_integerFieldIsRequiredAndNotEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true")
                .param("catQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_integerFieldIsRequiredAndEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true")
                .param("catQuantity", ""))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "catQuantity"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_integerFieldIsRequiredAndWhitespace() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true")
                .param("catQuantity", "  "))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "catQuantity"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_integerFieldIsRequiredAndNull() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "catQuantity"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_collectionFieldIsRequiredAndNotEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasFoodAllergies", "true")
                .param("foodAllergies[0]", " nuts ")
                .param("foodAllergies[1]", " fish "))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_collectionFieldIsRequiredAndEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasFoodAllergies", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "foodAllergies"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_optionalFieldIsRequiredAndNull() throws Exception {
        TestDTO testResource = new TestDTO();
        testResource.setDogPossiblyHasName(true);

        Set<ConstraintViolation<TestDTO>> result = localValidatorFactory.validate(testResource);

        assertEquals(1, result.size());
        assertEquals(result.stream().findFirst().get().getMessage(), "{validation.testform.dogname.required}");
    }

    @Test
    public void isValid_optionalFieldIsRequiredAndNotPresent() throws Exception {
        TestDTO testResource = new TestDTO();
        testResource.setDogPossiblyHasName(true);
        testResource.setPossibleDogName(Optional.empty());

        Set<ConstraintViolation<TestDTO>> result = localValidatorFactory.validate(testResource);

        assertEquals(1, result.size());
        assertEquals(result.stream().findFirst().get().getMessage(), "{validation.testform.dogname.required}");
    }

    @Test
    public void isValid_optionalFieldIsRequiredAndPresent() throws Exception {
        TestDTO testResource = new TestDTO();
        testResource.setDogPossiblyHasName(true);

        String dogName = "test";
        testResource.setPossibleDogName(Optional.of(dogName));

        Set<ConstraintViolation<TestDTO>> result = localValidatorFactory.validate(testResource);

        assertEquals(0, result.size());
     }

    @Test
    public void isValid_optionalFieldIsNotRequiredAndNull() throws Exception {
        TestDTO testResource = new TestDTO();
        testResource.setDogPossiblyHasName(false);

        Set<ConstraintViolation<TestDTO>> result = localValidatorFactory.validate(testResource);

        assertEquals(0, result.size());
     }

    @Test
    public void isValid_fieldsAreNotRequired() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasFoodAllergies", "false")
                .param("anythingElseToDeclare", "false")
                .param("hasCats", "false"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @FieldRequiredIf(required = "foodAllergies", argument = "hasFoodAllergies", predicate = true, message = "{validation.testform.foodAllergies.required}")
    @FieldRequiredIf(required = "pleaseGiveFurtherDetails", argument = "anythingElseToDeclare", predicate = true, message = "{validation.testform.pleasegivefurtherdetails.required}")
    @FieldRequiredIf(required = "catQuantity", argument = "hasCats", predicate = true, message="{validation.testform.catquantity.required}")
    public static class TestForm {

        private Boolean hasFoodAllergies;
        private List<String> foodAllergies;

        private Boolean anythingElseToDeclare;
        private String pleaseGiveFurtherDetails;

        private Boolean hasCats;
        private Integer catQuantity;


        public TestForm() {
        }

        public Boolean getHasFoodAllergies() {
            return hasFoodAllergies;
        }

        public void setHasFoodAllergies(Boolean hasFoodAllergies) {
            this.hasFoodAllergies = hasFoodAllergies;
        }

        public List<String> getFoodAllergies() {
            return foodAllergies;
        }

        public void setFoodAllergies(List<String> foodAllergies) {
            this.foodAllergies = foodAllergies;
        }

        public Boolean getAnythingElseToDeclare() {
            return anythingElseToDeclare;
        }

        public void setAnythingElseToDeclare(Boolean anythingElseToDeclare) {
            this.anythingElseToDeclare = anythingElseToDeclare;
        }

        public String getPleaseGiveFurtherDetails() {
            return pleaseGiveFurtherDetails;
        }

        public void setPleaseGiveFurtherDetails(String pleaseGiveFurtherDetails) {
            this.pleaseGiveFurtherDetails = pleaseGiveFurtherDetails;
        }

        public Boolean getHasCats() {
            return hasCats;
        }

        public void setHasCats(Boolean hasCats) {
            this.hasCats = hasCats;
        }

        public Integer getCatQuantity() {
            return catQuantity;
        }

        public void setCatQuantity(Integer catQuantity) {
            this.catQuantity = catQuantity;
        }
    }

    @FieldRequiredIf(required = "possibleDogName", argument = "dogPossiblyHasName", predicate = true, message="{validation.testform.dogname.required}")
    public static class TestDTO {
        public TestDTO() {}

        private Boolean dogPossiblyHasName;
        private Optional<String> possibleDogName;

        public Boolean getDogPossiblyHasName() {
            return dogPossiblyHasName;
        }

        public void setDogPossiblyHasName(Boolean dogPossiblyHasName) {
            this.dogPossiblyHasName = dogPossiblyHasName;
        }

        public Optional<String> getPossibleDogName() {
            return possibleDogName;
        }

        public void setPossibleDogName(Optional<String> possibleDogName) {
            this.possibleDogName = possibleDogName;
        }
    }

    @Controller
    @RequestMapping("/")
    public class TestController {

        @PostMapping
        public ModelAndView test(@Valid @ModelAttribute("form") TestForm form, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("failure");
            }
            return new ModelAndView("success");
        }
    }
}
