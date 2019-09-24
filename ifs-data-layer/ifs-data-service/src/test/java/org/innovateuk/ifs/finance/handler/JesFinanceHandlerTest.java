package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JesFinanceHandlerTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private JesFinanceHandler organisationJESFinance;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Test(expected = RuntimeException.class)
    public void updateCost_shouldReturnFailureWhenCostItemCannotBeFoundById() {
        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().build();

        when(applicationFinanceRowRepository.findById(any())).thenReturn(Optional.empty());

        ApplicationFinanceRow result = organisationJESFinance.updateCost(applicationFinanceRow);

        assertNull(result);

        verify(applicationFinanceRowRepository, times(0)).save(any(ApplicationFinanceRow.class));
        verifyNoMoreInteractions(applicationFinanceRowRepository);
    }

    @Test
    public void updateCost_shouldSaveEntityWhenCostItemCanBeFoundById() {
        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withId(1L).build();

        when(applicationFinanceRowRepository.findById(any())).thenReturn(Optional.of(applicationFinanceRow));
        when(applicationFinanceRowRepository.save(any(ApplicationFinanceRow.class))).thenReturn(applicationFinanceRow);

        ApplicationFinanceRow result = organisationJESFinance.updateCost(applicationFinanceRow);

        assertEquals(applicationFinanceRow,result);

        verify(applicationFinanceRowRepository, times(1)).findById(any());
        verify(applicationFinanceRowRepository, times(1)).save(any(ApplicationFinanceRow.class));
        verifyNoMoreInteractions(applicationFinanceRowRepository);
    }

    @Test
    public void addCost_shouldSaveEntityWhenAFinanceRowForNameCannotBeFound() {
        String financeRowName = "unique-name";

        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withName(financeRowName).withTarget(newApplicationFinance().build())
                .withType(FinanceRowType.LABOUR)
                .withId(1L).build();

        when(applicationFinanceRowRepository.findById(any())).thenReturn(Optional.empty());
        when(applicationFinanceRowRepository.save(any(ApplicationFinanceRow.class))).thenReturn(applicationFinanceRow);

        ApplicationFinanceRow result = organisationJESFinance.addCost(applicationFinanceRow);

        assertEquals(applicationFinanceRow,result);

        verify(applicationFinanceRowRepository, times(1)).save(applicationFinanceRow);
        verifyNoMoreInteractions(applicationFinanceRowRepository);
    }
}