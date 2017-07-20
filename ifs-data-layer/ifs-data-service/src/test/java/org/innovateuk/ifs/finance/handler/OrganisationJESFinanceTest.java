package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class OrganisationJESFinanceTest {
    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @InjectMocks
    private OrganisationJESFinance organisationJESFinance;


    @Test
    public void updateCost_shouldReturnFailureWhenCostItemCannotBeFoundById() throws Exception {

    }

    @Test
    public void updateCost_shouldSaveEntityWhenCostItemCanBeFoundById() throws Exception {

    }

    @Test
    public void addCost_shouldReturnFailureWhenAFinanceRowForNameCanBeFound() throws Exception {

    }

    @Test
    public void addCost_shouldSaveEntityWhenAFinanceRowForNameCannotBeFound() throws Exception {

    }
}