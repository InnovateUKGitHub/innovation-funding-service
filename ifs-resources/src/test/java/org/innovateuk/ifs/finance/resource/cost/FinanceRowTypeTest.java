package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FinanceRowTypeTest {

    @Test
    public void getKtpFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getKtpFinanceRowTypes();

        assertNotNull(financeRowTypes);
        assertEquals(Arrays.asList(FinanceRowType.OTHER_COSTS,
                FinanceRowType.FINANCE,
                FinanceRowType.ASSOCIATE_SALARY_COSTS,
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                FinanceRowType.CONSUMABLES,
                FinanceRowType.ASSOCIATE_SUPPORT,
                FinanceRowType.KNOWLEDGE_BASE,
                FinanceRowType.ESTATE_COSTS,
                FinanceRowType.KTP_TRAVEL,
                FinanceRowType.ADDITIONAL_COMPANY_COSTS,
                FinanceRowType.PREVIOUS_FUNDING,
                FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                FinanceRowType.INDIRECT_COSTS), financeRowTypes);
    }

    @Test
    public void getFecSpecificFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getFecSpecificFinanceRowTypes();

        assertNotNull(financeRowTypes);
        assertEquals(Arrays.asList(FinanceRowType.ASSOCIATE_SUPPORT,
                FinanceRowType.KNOWLEDGE_BASE,
                FinanceRowType.ESTATE_COSTS), financeRowTypes);

        financeRowTypes.forEach(financeRowType -> assertTrue(financeRowType.isIncludedInSpendProfile()));
    }

    @Test
    public void getNonFecSpecificFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getNonFecSpecificFinanceRowTypes();

        assertNotNull(financeRowTypes);
        assertEquals(Arrays.asList(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                FinanceRowType.INDIRECT_COSTS), financeRowTypes);

        financeRowTypes.forEach(financeRowType -> assertTrue(financeRowType.isIncludedInSpendProfile()));
    }
}
