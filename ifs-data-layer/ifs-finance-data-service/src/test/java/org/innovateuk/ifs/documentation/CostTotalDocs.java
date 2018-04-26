package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CostTotalDocs {

    public static final FieldDescriptor[] financeCostTotalResourceFields = {
            fieldWithPath("financeType").description("The name of finance type e.g. APPLICATION or PROJECT"),
            fieldWithPath("financeRowType").description("The name of the finance row type e.g. MATERIALS or LABOUR"),
            fieldWithPath("total").description("The finance total in GBP"),
            fieldWithPath("financeId").description("The id of the related FinanceRow in the data-service"),
    };

    public static final FinanceCostTotalResourceBuilder financeCostTotalResourceBuilder = newFinanceCostTotalResource()
            .withFinanceType(FinanceType.APPLICATION)
            .withFinanceRowType(FinanceRowType.MATERIALS)
            .withTotal(new BigDecimal("150000.123456"))
            .withFinanceId(1L);
}
