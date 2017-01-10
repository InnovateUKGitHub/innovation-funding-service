package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.mapper.GuidanceRowMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.application.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_COUNT;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_TURNOVER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the CompetitionSetupFinanceServiceImpl with mocked repository.
 */
public class CompetitionSetupFinanceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupFinanceServiceImpl> {

    @Override
    protected CompetitionSetupFinanceServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupFinanceServiceImpl();
    }

    @Mock
    private FormInputRepository formInputRepository;


    @Test
    public void test_save() {
        long competitionId = 1L;

        CompetitionSetupFinanceResource resource = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withIncludeGrowthTable(false)
                .withFullApplicationFinance(false)
                .build();

        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(false).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(STAFF_TURNOVER).withActive(false).build();
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT, STAFF_TURNOVER))).thenReturn(asList(staffCountFormInput, staffTurnoverFormInput));
        service.save(resource);
        assertTrue(staffCountFormInput.getActive());
        assertTrue(staffTurnoverFormInput.getActive());
    }
}
