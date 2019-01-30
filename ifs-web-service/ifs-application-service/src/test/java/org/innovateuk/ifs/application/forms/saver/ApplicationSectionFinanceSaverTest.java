package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_SECTION_AS_COMPLETE;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_SECTION_AS_INCOMPLETE;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link YourProjectCostsCompleter}
 */

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSectionFinanceSaverTest {

    private ApplicationSectionFinanceSaver saver = new ApplicationSectionFinanceSaver();

    @Test
    public void handleStateAid_MarkAsComplete() {
        final Map<String, String[]> params = new HashMap<>();
        final ApplicationResource application = newApplicationResource().with(applicationResource -> applicationResource.setStateAidAgreed(null)).build();
        final ApplicationForm form = new ApplicationForm();
        form.setStateAidAgreed(Boolean.TRUE);
        final SectionResource selectedSection = newSectionResource().withType(FINANCE).build();

        saver.handleStateAid(params, application, form, selectedSection);
        assertEquals(null, application.getStateAidAgreed());

        params.put(MARK_SECTION_AS_COMPLETE, null);
        saver.handleStateAid(params, application, form, selectedSection);
        assertEquals(Boolean.TRUE, application.getStateAidAgreed());
    }

    @Test
    public void handleStateAid_MarkAsInComplete() {
        final Map<String, String[]> params = new HashMap<>();
        final ApplicationResource application = newApplicationResource().with(applicationResource -> applicationResource.setStateAidAgreed(null)).build();
        final ApplicationForm form = new ApplicationForm();
        form.setStateAidAgreed(Boolean.TRUE);
        final SectionResource selectedSection = newSectionResource().withType(FINANCE).build();

        params.put(MARK_SECTION_AS_INCOMPLETE, null);
        saver.handleStateAid(params, application, form, selectedSection);

        assertEquals(Boolean.FALSE, application.getStateAidAgreed());
    }
}