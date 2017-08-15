package org.innovateuk.ifs.registration;

import org.apache.commons.lang3.CharEncoding;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.mockito.Mock;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

//TODO: Fix tests
public class OrganisationCreationInitializationControllerTest extends BaseControllerMockMVCTest<OrganisationCreationInitializationController> {
    protected OrganisationCreationInitializationController supplyControllerUnderTest() {
        return new OrganisationCreationInitializationController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    //@Test
    public void testFindBusiness() throws Exception {

        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(new OrganisationCreationForm()));

        Cookie[] cookies = mockMvc.perform(get("/organisation/create/initialize"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn().getResponse().getCookies();

        assertEquals(2, cookies.length);
        assertNotNull(cookies[0]);
        assertNotNull(cookies[1]);
        assertEquals("", Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("flashMessage")).findAny().get().getValue());
        assertEquals(URLEncoder.encode("{\"organisationType\":1,\"leadApplicant\":true}", CharEncoding.UTF_8),
                getDecryptedCookieValue(cookies, "organisationType"));
    }
}