package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.transactional.template.CompetitionSetupTemplateService;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorServiceImpl;
import org.junit.Test;

public class CompetitionSetupTemplateServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTemplateService>{

    public CompetitionSetupTemplateService supplyServiceUnderTest() {
        return new CompetitionTemplatePersistorServiceImpl();
    }

    @Test
    public void addDefaultQuestionToCompetition() throws Exception {

    }

    @Test
    public void createCompetitionByCompetitionTemplate() throws Exception {

    }
}