package org.innovateuk.ifs.eugrant.contact.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.eugrant.builder.EuContactResourceBuilder.newEuContactResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EuContactFormPopulatorTest extends BaseServiceUnitTest<EuContactFormPopulator> {

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Override
    protected EuContactFormPopulator supplyServiceUnderTest() {
        return new EuContactFormPopulator();
    }

    @Test
    public void populateContactForm() throws Exception {

        EuContactResource euContactResource = newEuContactResource().build();
        EuContactForm contactForm = new EuContactForm();

        euContactResource.setName("worth test");
        euContactResource.setEmail("worth@gmail.com");
        euContactResource.setJobTitle("worth employee");
        euContactResource.setTelephone("0123456789");

        EuGrantResource euGrantResource = newEuGrantResource()
                .withContact(euContactResource)
                .build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        EuContactForm euContactForm = service.populate(contactForm);

        assertEquals("worth test", euContactForm.getName());
        assertEquals("worth@gmail.com", euContactForm.getEmail());
        assertEquals("worth employee", euContactForm.getJobTitle());
        assertEquals("0123456789", euContactForm.getTelephone());
    }
}
