package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.junit.Test;

public class CompetitionSetupTemplateServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTemplateService>{
    public CompetitionSetupTemplateService supplyServiceUnderTest() {
        return new CompetitionSetupTemplateServiceImpl();
    }

    @Test
    public void createCompetitionByCompetitionTemplate1() throws Exception {
    }

    @Test
    public void createDefaultForApplicationSection() throws Exception {
    }

    @Test
    public void deleteQuestionInApplicationSection() throws Exception {
    }
}