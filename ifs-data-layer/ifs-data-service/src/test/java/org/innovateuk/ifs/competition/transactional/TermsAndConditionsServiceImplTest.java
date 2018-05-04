package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.junit.Test;
import org.mockito.InjectMocks;

public class TermsAndConditionsServiceImplTest extends BaseServiceUnitTest<TermsAndConditionsServiceImpl> {

    @InjectMocks
    private TermsAndConditionsServiceImpl service;

    @Override
    protected TermsAndConditionsServiceImpl supplyServiceUnderTest() {
        return new TermsAndConditionsServiceImpl();
    }

    @Test
    public void test_getTemplateById() {

    }

    @Test
    public void test_getTemplateByNonExistingId() {

    }

    @Test
    public void test_getTemplateByNull() {

    }

    @Test
    public void test_getLatestVersionsForAllTermsAndConditions() {

    }

    @Test
    public void test_getLatestVersionsForAllTermsAndConditionsWithoutResult() {

    }

}
