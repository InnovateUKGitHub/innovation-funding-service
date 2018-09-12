package org.innovateuk.ifs.commons.validation;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.function.BiPredicate;

import static org.innovateuk.ifs.commons.validation.matchers.ExtendedMockMvcResultMatchers.model;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class FieldComparisonValidatorTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private FieldComparisonValidatorTest.TestController controller = new FieldComparisonValidatorTest.TestController();
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller).build();

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void isValid() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true")
                .param("catQuantity", "10")
                .param("hasDogs", "true")
                .param("dogQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_notHavingBothDogsAndCatsReturnsError() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasDogs", "false")
                .param("hasCats", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorMessage("form", "hasDogs", "{needs.both.dogs.and.cats}"))
                .andExpect(model().attributeHasNoFieldErrors("form", "hasCats"))
                .andExpect(view().name("failure"))
                .andReturn();
    }

    @Test
    public void isValid_havingBothDogsAndCatsReturnsNoErrors() throws Exception{
        MvcResult result = mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("hasCats", "true")
                .param("hasDogs", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"))
                .andReturn();
    }

    @Test
    public void isValid_notHavingAnEqualAmountOfDogsAndCatsReturnsError() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("catQuantity", "10")
                .param("dogQuantity", "11"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorMessage("form", "dogQuantity", "{needs.equal.amount.of.dogs.and.cats}"))
                .andExpect(model().attributeHasNoFieldErrors("form", "catQuantity"))
                .andExpect(view().name("failure"))
                .andReturn();
    }

    @Test
    public void isValid_havingAnEqualAmountOfDogsAndCatsReturnsNoErrors() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("catQuantity", "10")
                .param("dogQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"))
                .andReturn();
    }

    @Test
    public void isValid_nullValuesResultInNoErrors() throws Exception {
        MvcResult result = mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"))
                .andReturn();
    }

    @FieldComparison(
            firstField = "hasDogs",
            secondField = "hasCats",
            predicate = FieldComparisonValidatorTest.TestForm.HasDogsAndCatsComparison.class,
            message = "{needs.both.dogs.and.cats}")
    @FieldComparison(
            firstField = "dogQuantity",
            secondField = "catQuantity",
            predicate = FieldComparisonValidatorTest.TestForm.DogsEqualsCatsComparison.class,
            message = "{needs.equal.amount.of.dogs.and.cats}")
    public static class TestForm {
        private Boolean hasDogs;
        private Integer dogQuantity;

        private Boolean hasCats;
        private Integer catQuantity;


        public TestForm() {
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

        public Boolean getHasDogs() {
            return hasDogs;
        }

        public void setHasDogs(Boolean hasDogs) {
            this.hasDogs = hasDogs;
        }

        public Integer getDogQuantity() {
            return dogQuantity;
        }

        public void setDogQuantity(Integer dogQuantity) {
            this.dogQuantity = dogQuantity;
        }

        public static class DogsEqualsCatsComparison implements BiPredicateProvider<Integer, Integer> {
            public DogsEqualsCatsComparison() {}

            public BiPredicate<Integer, Integer> predicate()  {
               return (dogs, cats) -> cats.equals(dogs);
            }
        }

        public static class HasDogsAndCatsComparison implements BiPredicateProvider<Boolean, Boolean> {
            public HasDogsAndCatsComparison() {}

            public BiPredicate<Boolean, Boolean> predicate()  {
                return (hasDogs, hasCats) -> hasDogs && hasCats;
            }
        }
    }

    @Controller
    @RequestMapping("/")
    public class TestController {

        @PostMapping
        public ModelAndView test(@Valid @ModelAttribute("form") FieldComparisonValidatorTest.TestForm form, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("failure");
            }
            return new ModelAndView("success");
        }
    }
}