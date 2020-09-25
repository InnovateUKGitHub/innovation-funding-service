package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.stream;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.GENERIC;
import static org.junit.Assert.assertFalse;

@Transactional
@Rollback
public class CompetitionSetupTemplateServiceImplIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private CompetitionSetupTemplateServiceImpl service;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    public void allTemplateTypes() {
        setLoggedInUser(getIfsAdmin());

        stream(CompetitionTypeEnum.values()).forEach(type -> {
            Competition competition = competitionRepository.save(newCompetition()
                    .withId(null)
                    .withFundingType(FundingType.GRANT)
                    .build());

            CompetitionType competitionType = competitionTypeRepository.findByName(type.getText());

            ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

            assertFalse(result.getSuccess().getSections().isEmpty());
        });
    }

    @Test
    public void allFundingType() {
        setLoggedInUser(getIfsAdmin());

        stream(FundingType.values()).forEach(type -> {
            Competition competition = competitionRepository.save(newCompetition()
                    .withId(null)
                    .withFundingType(type)
                    .build());

            CompetitionType competitionType = competitionTypeRepository.findByName(GENERIC.getText());

            ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

            assertFalse(result.getSuccess().getProjectSetupStages().isEmpty());
            assertFalse(result.getSuccess().getFinanceRowTypes().isEmpty());
        });
    }
}