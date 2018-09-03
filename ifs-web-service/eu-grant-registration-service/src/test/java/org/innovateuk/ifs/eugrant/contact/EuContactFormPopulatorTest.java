package org.innovateuk.ifs.eugrant.contact;

import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.contact.populator.EuContactFormPopulator;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class EuContactFormPopulatorTest {

    @Mock
    private EuContactFormPopulator euContactFormPopulator;

    @Test
    public void populateContactForm() throws Exception {

        EuContactResource euContactResource = new EuContactResource();

        euContactResource.setName("worth test");
        euContactResource.setEmail("worth@gmail.com");
        euContactResource.setJobTitle("worth employee");
        euContactResource.setTelephone("0123456789");

        EuContactForm euContactForm = euContactFormPopulator.populate(euContactResource);

        assertEquals("worth test", euContactForm.getName());
        assertEquals("worth@gmail.com", euContactForm.getEmail());
        assertEquals("worth employee", euContactForm.getJobTitle());
        assertEquals("0123456789", euContactForm.getTelephone());
    }
}
