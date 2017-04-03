package org.innovateuk.ifs.commons.validation;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
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

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ValidAggregatedDateTest {
    private ValidAggregatedDateTest.TestController controller = new ValidAggregatedDateTest.TestController();

    private LocalValidatorFactoryBean localValidatorFactory;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller).build();

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void emptyFieldResultsYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "")
                .param("month", "01")
                .param("day", "01"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

    }

    @Test
    public void missingFieldResultsYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("month", "12")
                .param("day", "28"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

    }

    @Test
    public void negativeDayFieldYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2016")
                .param("month", "01")
                .param("day", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void negativeMonthFieldYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2016")
                .param("month", "-1")
                .param("day", "01"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void impossibleLeapYearDateYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2017")
                .param("month", "2")
                .param("day", "29"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void possibleLeapYearDateYieldsValidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2016")
                .param("month", "2")
                .param("day", "29"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void impossibleMonthDateYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2017")
                .param("month", "13")
                .param("day", "28"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void impossibleDayDateYieldsInvalidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2017")
                .param("month", "01")
                .param("day", "32"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void possibleDateYieldsValidResult() throws Exception  {
        mockMvc.perform(post("/")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("year", "2017")
                .param("month", "2")
                .param("day", "28"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors());
    }

    @ValidAggregatedDate(dayField = "day", monthField = "month", yearField = "year", message = "{stuff.is.not.valid}")
    public static class TestForm {
        private Integer year;
        private Integer month;
        private Integer day;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }
    }

    @Controller
    @RequestMapping("/")
    public class TestController {

        @PostMapping
        public ModelAndView test(@Valid @ModelAttribute("form") ValidAggregatedDateTest.TestForm form, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return new ModelAndView("failure");
            }
            return new ModelAndView("success");
        }
    }
}
