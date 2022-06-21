package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class FinanceRowTypeTest {

    @Test
    public void getKtpFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getKtpFinanceRowTypes();

        assertNotNull(financeRowTypes);
        assertEquals(asList(FinanceRowType.OTHER_COSTS,
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
        assertEquals(asList(FinanceRowType.ASSOCIATE_SUPPORT,
                FinanceRowType.KNOWLEDGE_BASE,
                FinanceRowType.ESTATE_COSTS), financeRowTypes);

        financeRowTypes.forEach(financeRowType -> assertTrue(financeRowType.isIncludedInSpendProfile()));
    }

    @Test
    public void getNonFecSpecificFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getNonFecSpecificFinanceRowTypes();

        assertNotNull(financeRowTypes);
        assertEquals(asList(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                FinanceRowType.INDIRECT_COSTS), financeRowTypes);

        financeRowTypes.forEach(financeRowType -> assertTrue(financeRowType.isIncludedInSpendProfile()));
    }

    @Test
    public void getHecpSpecificFinanceRowTypes() {
        List<FinanceRowType> financeRowTypes = FinanceRowType.getHecpSpecificFinanceRowTypes();
        assertNotNull(financeRowTypes);
        assertEquals(asList(
                FinanceRowType.PERSONNEL,
                FinanceRowType.SUBCONTRACTING_COSTS,
                FinanceRowType.TRAVEL,
                FinanceRowType.EQUIPMENT,
                FinanceRowType.OTHER_GOODS,
                FinanceRowType.OTHER_COSTS,
                FinanceRowType.HECP_INDIRECT_COSTS), financeRowTypes);
    }
}
