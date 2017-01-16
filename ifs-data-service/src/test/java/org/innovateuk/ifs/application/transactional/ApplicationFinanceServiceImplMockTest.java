package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.sectionupdater.ApplicationFinanceOrganisationUpdaterTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationFinanceServiceImplMockTest extends BaseServiceUnitTest<ApplicationFinanceService> {

    @Override
    protected ApplicationFinanceService supplyServiceUnderTest() {
        return new ApplicationFinanceServiceImpl();
    }

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "financeSectionSavers", asMap(SectionType.ORGANISATION_FINANCES, ApplicationFinanceOrganisationUpdaterTest.class));
    }

    @Test
    public void testGetUpdaterBySectionType() {
        assertEquals(Optional.of(ApplicationFinanceOrganisationUpdaterTest.class), service.getApplicationFinanceSaver(SectionType.ORGANISATION_FINANCES));
    }

    @Test
    public void testGetUpdaterByUnknowSectionType() {
        assertEquals(Optional.empty(), service.getApplicationFinanceSaver(SectionType.GENERAL));
    }
}
