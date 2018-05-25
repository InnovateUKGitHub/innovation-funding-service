package org.innovateuk.ifs.content;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.content.form.NewSiteTermsAndConditionsForm;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder
        .newSiteTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SiteTermsControllerTest extends BaseControllerMockMVCTest<SiteTermsController> {

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Mock
    private UserService userService;

    @Mock
    private CookieUtil cookieUtil;

    @Override
    protected SiteTermsController supplyControllerUnderTest() {
        return new SiteTermsController();
    }

    @Test
    public void termsAndConditions() throws Exception {
        SiteTermsAndConditionsResource siteTermsAndConditionsResource = newSiteTermsAndConditionsResource()
                .withTemplate("test-terms-and-conditions")
                .build();

        when(termsAndConditionsRestService.getLatestSiteTermsAndConditions()).thenReturn(
                restSuccess(siteTermsAndConditionsResource));

        mockMvc.perform(get("/info/terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(view().name("content/test-terms-and-conditions"));
    }

    @Test
    public void newTermsAndConditions() throws Exception {
        NewSiteTermsAndConditionsForm expectedForm = new NewSiteTermsAndConditionsForm();

        mockMvc.perform(get("/info/new-terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("content/new-terms-and-conditions"));
    }

    @Test
    public void agreeNewTermsAndConditions() throws Exception {
        long userId = 1L;
        String expectedRedirectUrl = "/application/99/summary";

        when(userService.agreeNewTermsAndConditions(userId)).thenReturn(serviceSuccess());
        when(cookieUtil.getCookieValue(isA(HttpServletRequest.class), eq("savedRequestUrl")))
                .thenReturn(expectedRedirectUrl);

        mockMvc.perform(post("/info/new-terms-and-conditions")
                .param("agree", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/99/summary"));

        InOrder inOrder = inOrder(cookieUtil, userService);
        inOrder.verify(userService).agreeNewTermsAndConditions(userId);
        inOrder.verify(cookieUtil).getCookieValue(isA(HttpServletRequest.class), eq("savedRequestUrl"));
        inOrder.verify(cookieUtil).removeCookie(isA(HttpServletResponse.class), eq("savedRequestUrl"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void agreeNewTermsAndConditions_notAgreed() throws Exception {
        MvcResult result = mockMvc.perform(post("/info/new-terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeHasFieldErrors("form", "agree"))
                .andExpect(model().attributeHasFieldErrorCode("form", "agree", "NotNull"))
                .andExpect(view().name("content/new-terms-and-conditions"))
                .andReturn();

        NewSiteTermsAndConditionsForm form = (NewSiteTermsAndConditionsForm) result.getModelAndView().getModel()
                .get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("agree"));
        assertEquals("In order to continue you must agree to the terms and conditions.", bindingResult.getFieldError
                ("agree").getDefaultMessage());

        verifyNoMoreInteractions(userService);
    }

    @Test
    public void agreeNewTermsAndConditions_noRedirectUrlCookieRedirectsToDashboard() throws Exception {
        long userId = 1L;

        when(userService.agreeNewTermsAndConditions(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/info/new-terms-and-conditions")
                .param("agree", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        InOrder inOrder = inOrder(cookieUtil, userService);
        inOrder.verify(userService).agreeNewTermsAndConditions(userId);
        inOrder.verify(cookieUtil).getCookieValue(isA(HttpServletRequest.class), eq("savedRequestUrl"));
        inOrder.verify(cookieUtil).removeCookie(isA(HttpServletResponse.class), eq("savedRequestUrl"));
        inOrder.verifyNoMoreInteractions();
    }
}