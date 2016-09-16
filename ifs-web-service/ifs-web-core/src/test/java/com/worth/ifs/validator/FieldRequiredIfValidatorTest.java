package com.worth.ifs.validator;

import com.worth.ifs.validator.constraints.FieldRequiredIf;
import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class FieldRequiredIfValidatorTest {

    private TestController controller = new TestController();

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void isValid_fieldIsRequiredAndNotEmpty() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "true")
                .param("pleaseGiveFurtherDetails", " some further details "))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_fieldIsRequiredAndEmpty() throws Exception {
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
    public void isValid_fieldIsRequiredAndWhitespace() throws Exception {
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
    public void isValid_fieldIsRequiredAndNull() throws Exception {
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
    public void isValid_fieldIsNotRequired() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("anythingElseToDeclare", "false"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @FieldRequiredIf(required = "pleaseGiveFurtherDetails", argument = "anythingElseToDeclare", predicate = true, message = "{validation.testform.pleasegivefurtherdetails.required}")
    public static class TestForm {

        private Boolean anythingElseToDeclare;
        private String pleaseGiveFurtherDetails;

        public TestForm() {
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
    }

    @Controller
    @RequestMapping("/")
    public class TestController {

        @RequestMapping(method = RequestMethod.POST)
        public ModelAndView test(@Valid @ModelAttribute("form") TestForm form, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("failure");
            }
            return new ModelAndView("success");
        }
    }
}