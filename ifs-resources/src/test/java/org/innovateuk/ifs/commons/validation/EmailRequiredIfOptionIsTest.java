package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.validation.constraints.EmailRequiredIfOptionIs;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.hamcrest.Matchers.*;

/**
 * Created by danielsmith on 12/01/2017.
 */
public class EmailRequiredIfOptionIsTest {

    private EmailRequiredIfOptionIsTest.TestController controller = new EmailRequiredIfOptionIsTest.TestController();

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void isValid_EmailNeedsValidatingIsValid() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("emailRegexp", "test@test.com")
                .param("user", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_PredicateNotSet() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("emailRegexp", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_EmailDoesNotNeedValidating() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("emailRegexp", "test@test.com")
                .param("user", "1"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("success"));
    }

    @Test
    public void isValid_EmailNeedsValidatingIsNotValid() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", "test@")
                .param("emailRegexp", "test@test.com")
                .param("user", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "email"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_EmailNeedsValidatingFailsRegex() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("emailRegexp", "test@t1ßst.com")
                .param("user", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "emailRegexp"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_EmailNeedsValidatingIsBlank() throws Exception {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("email", " ")
                .param("emailRegexp", "test@test.com")
                .param("user", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("form", "email"))
                .andExpect(view().name("failure"));
    }

    @Test
    public void isValid_EmailNeedsValidatingNotSet() throws Exception {
        List<String> errors = Arrays.asList("EmailRequiredIfOptionIs.form", "EmailRequiredIfOptionIs");

        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("emailRegexp", "test@test.com")
                .param("user", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(view().name("failure"));
    }

    @EmailRequiredIfOptionIs(required = "email", argument = "user", predicate = -1L, regexp = ".*", message = "{validation.project.invite.email.required}", invalidMessage= "{validation.project.invite.email.invalid}")
    @EmailRequiredIfOptionIs(required = "emailRegexp", argument = "user", predicate = -1L, regexp = "^[^1]*$", message = "{validation.project.invite.email.required}", invalidMessage= "{validation.project.invite.email.invalid}")
    public static class TestForm {

        private String email;

        private String emailRegexp;

        private Long user;


        public TestForm() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmailRegexp() {
            return emailRegexp;
        }

        public void setEmailRegexp(String email) {
            this.emailRegexp = email;
        }

        public Long getUser() {
            return user;
        }

        public void setUser(Long user) {
            this.user = user;
        }

    }

    @Controller
    @RequestMapping("/")
    public class TestController {

        @RequestMapping(method = RequestMethod.POST)
        public ModelAndView test(@Valid @ModelAttribute("form") EmailRequiredIfOptionIsTest.TestForm form, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("failure");
            }
            return new ModelAndView("success");
        }
    }
}
