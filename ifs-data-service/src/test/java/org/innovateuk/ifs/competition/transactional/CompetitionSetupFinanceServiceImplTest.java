package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
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
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
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

    @Test
    public void test_save() {
        long competitionId = 1L;
        boolean isFullFinance = false;
        boolean isIncludeGrowthTable = false;

        CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withIncludeGrowthTable(isFullFinance)
                .withFullApplicationFinance(isIncludeGrowthTable)
                .build();

        // Make sure the the booleans in the competition and the form inputs are the negation of what we are changing
        // them to so that we can check they've been changed. Note that isIncludeGrowthTable being true should result in
        // deactivated form inputs.
        Competition c = newCompetition().with(id(competitionId)).withFullFinance(!isFullFinance).build();
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(isIncludeGrowthTable).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(STAFF_TURNOVER).withActive(isIncludeGrowthTable).build();
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_TURNOVER))).thenReturn(asList(staffTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(asList(staffCountFormInput));
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(c);
        // Method under test
        ServiceResult<Void> save = service.save(csfr);
        // Assertions
        assertTrue(save.isSuccess());
        assertEquals(isFullFinance, c.isFullApplicationFinance());
        assertEquals(isIncludeGrowthTable, !staffCountFormInput.getActive());
        assertEquals(isIncludeGrowthTable, !staffTurnoverFormInput.getActive());
    }

    @Test
    public void test_GetForCompetition() {
        boolean staffCountAndTurnoverActive = true;
        boolean isFullFinance = true;
        Long competitionId = 1L;
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(staffCountAndTurnoverActive).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(STAFF_TURNOVER).withActive(staffCountAndTurnoverActive).build();
        Competition c = newCompetition().with(id(competitionId)).withFullFinance(isFullFinance).build();
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_TURNOVER))).thenReturn(asList(staffTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(asList(staffCountFormInput));
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(c);
        // Method under test
        ServiceResult<CompetitionSetupFinanceResource> getForCompetition = service.getForCompetition(competitionId);
        // Assertions
        assertTrue(getForCompetition.isSuccess());
        assertEquals(isFullFinance, getForCompetition.getSuccessObject().isFullApplicationFinance());
        assertEquals(!staffCountAndTurnoverActive, getForCompetition.getSuccessObject().isIncludeGrowthTable());
    }
}
