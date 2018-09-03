package org.innovateuk.ifs.eugrant.contact.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EuContactFormPopulatorTest extends BaseServiceUnitTest<EuContactFormPopulator> {

    @Override
    protected EuContactFormPopulator supplyServiceUnderTest() {
        return new EuContactFormPopulator();
    }

    @Test
    public void populateContactForm() throws Exception {

        EuContactResource euContactResource = new EuContactResource();

        euContactResource.setName("worth test");
        euContactResource.setEmail("worth@gmail.com");
        euContactResource.setJobTitle("worth employee");
        euContactResource.setTelephone("0123456789");

        EuContactForm euContactForm = service.populate(euContactResource);

        assertEquals("worth test", euContactForm.getName());
        assertEquals("worth@gmail.com", euContactForm.getEmail());
        assertEquals("worth employee", euContactForm.getJobTitle());
        assertEquals("0123456789", euContactForm.getTelephone());
    }
}
