package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OrganisationJESFinanceTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private OrganisationJESFinance organisationJESFinance = new OrganisationJESFinance();

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Test(expected = RuntimeException.class)
    public void updateCost_shouldReturnFailureWhenCostItemCannotBeFoundById() throws Exception {
        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().build();

        when(applicationFinanceRowRepository.findOne(any())).thenReturn(null);

        ApplicationFinanceRow result = organisationJESFinance.updateCost(applicationFinanceRow);

        assertNull(result);
        verify(applicationFinanceRowRepository, times(0)).save(any(ApplicationFinanceRow.class));
    }

    @Test
    public void updateCost_shouldSaveEntityWhenCostItemCanBeFoundById() throws Exception {
        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withId(1L).build();

        when(applicationFinanceRowRepository.findOne(any())).thenReturn(applicationFinanceRow);
        when(applicationFinanceRowRepository.save(any(ApplicationFinanceRow.class))).thenReturn(applicationFinanceRow);

        ApplicationFinanceRow result = organisationJESFinance.updateCost(applicationFinanceRow);

        assertEquals(applicationFinanceRow,result);
        verify(applicationFinanceRowRepository, times(1)).save(any(ApplicationFinanceRow.class));
    }

    @Test(expected = RuntimeException.class)
    public void addCost_shouldReturnFailureWhenAFinanceRowForNameCanBeFound() throws Exception {
        Long applicationFinanceId = 1L;
        Long questionId = 2L;
        String financeRowName = "unique-name";

        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withName(financeRowName).build();


        when(applicationFinanceRowRepository.findByTargetIdAndNameAndQuestionId(any(), any(), any())).thenReturn(Arrays.asList(applicationFinanceRow));

        ApplicationFinanceRow result = organisationJESFinance.addCost(applicationFinanceId, questionId, applicationFinanceRow);

        assertNull(result);
        verify(applicationFinanceRowRepository, times(0)).save(any(ApplicationFinanceRow.class));
    }

    @Test
    public void addCost_shouldSaveEntityWhenAFinanceRowForNameCannotBeFound() throws Exception {
        String financeRowName = "unique-name";

        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withName(financeRowName).withTarget(newApplicationFinance().build()).withQuestion(newQuestion().build()).withId(1L).build();

        when(applicationFinanceRowRepository.findOne(any())).thenReturn(null);
        when(applicationFinanceRowRepository.save(any(ApplicationFinanceRow.class))).thenReturn(applicationFinanceRow);

        ApplicationFinanceRow result = organisationJESFinance.addCost(applicationFinanceRow.getTarget().getId(), applicationFinanceRow.getQuestion().getId(), applicationFinanceRow);

        assertEquals(applicationFinanceRow,result);
        verify(applicationFinanceRowRepository, times(1)).save(applicationFinanceRow);
    }
}