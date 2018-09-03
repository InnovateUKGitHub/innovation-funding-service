package org.innovateuk.ifs.eugrant.contact;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.controller.EuContactDetailsController;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.contact.populator.EuContactFormPopulator;
import org.innovateuk.ifs.eugrant.contact.saver.EuContactSaver;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EuContactDetailsControllerTest extends BaseControllerMockMVCTest<EuContactDetailsController> {

    @Spy
    @InjectMocks
    private EuContactFormPopulator euContactFormPopulator;

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Mock
    private EuContactSaver euContactSaver;

    @Override
    protected EuContactDetailsController supplyControllerUnderTest() {
        return new EuContactDetailsController();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void viewContactDetails() throws Exception {

        EuGrantResource euGrantResource = new EuGrantResource();
        EuContactResource euContactResource = new EuContactResource();

        euContactResource.setName("worth user");
        euContactResource.setEmail("worth@gmail.com");
        euContactResource.setJobTitle("worth employee");
        euContactResource.setTelephone("0123456789");

        euGrantResource.setContact(euContactResource);

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        mockMvc.perform(get("/contact-details"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("eugrant/contact-details"));
    }

    @Test
    public void redirectToEditWhenContactDetailsNotFilledIn() throws Exception {

        EuGrantResource euGrantResource = new EuGrantResource();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        mockMvc.perform(get("/contact-details"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact-details/edit"));
    }

    @Test
    public void submitContactDetails() throws Exception {

        EuGrantResource euGrantResource = new EuGrantResource();

        EuContactForm contactForm = new EuContactForm();

        contactForm.setName("worth user");
        contactForm.setEmail("worth@gmail.com");
        contactForm.setJobTitle("worth employee");
        contactForm.setTelephone("0123456789");

        when(euGrantCookieService.get()).thenReturn(euGrantResource);
        when(euContactSaver.save(euGrantResource)).thenReturn(RestResult.restSuccess());

        mockMvc.perform(post("/contact-details")
                .param("name", contactForm.getName())
                .param("email", contactForm.getEmail())
                .param("jobTitle", contactForm.getJobTitle())
                .param("telephone", contactForm.getTelephone()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact-details"));
    }
}