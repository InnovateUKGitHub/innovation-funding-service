package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.repository.CostTotalRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.CostTotalBuilder.newCostTotal;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;

public class CostTotalServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CostTotalService costTotalService;

    @Autowired
    private CostTotalRepository costTotalRepository;

    @Before
    public void cleanRepository() {
        costTotalRepository.deleteAll();
    }

    @Test
    public void saveCostTotal() {
        FinanceCostTotalResource costTotalResource = newApplicationFinanceCostTotalResource(
                10L,
                LABOUR,
                new BigDecimal("1999.999999")
        );

        ServiceResult<Void> result = costTotalService.saveCostTotal(costTotalResource);

        assertThat(result.isSuccess()).isTrue();

        assertThat(costTotalRepository.count()).isOne();
        assertThat(costTotalRepository.findByFinanceId(costTotalResource.getFinanceId()))
                .isEqualToComparingOnlyGivenFields(
                        newCostTotal()
                                .withFinanceId(costTotalResource.getFinanceId())
                                .withName(costTotalResource.getFinanceRowType().getName())
                                .withType(costTotalResource.getFinanceType().name())
                                .withTotal(costTotalResource.getTotal())
                                .build()
                , "financeId", "name", "type", "total");
    }

    @Test
    public void saveCostTotals() {
        Long financeId = 20L;
        FinanceCostTotalResource costTotalResource1 = newApplicationFinanceCostTotalResource(
                financeId,
                LABOUR,
                new BigDecimal("5000.000000")
        );
        FinanceCostTotalResource costTotalResource2 = newApplicationFinanceCostTotalResource(
                financeId,
                MATERIALS,
                new BigDecimal("2500.000000")
        );

        List<FinanceCostTotalResource> costTotalResources = asList(costTotalResource1, costTotalResource2);

        ServiceResult<Void> result = costTotalService.saveCostTotals(costTotalResources);

        assertThat(result.isSuccess()).isTrue();

        assertThat(costTotalRepository.count()).isEqualTo(2);
        assertThat(costTotalRepository.findAllByFinanceId(financeId))
                .usingElementComparatorIgnoringFields("id")
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactlyInAnyOrder(
                        newCostTotal()
                                .withFinanceId(financeId)
                                .withName(
                                        costTotalResource1.getFinanceRowType().getName(),
                                        costTotalResource2.getFinanceRowType().getName()
                                )
                                .withType(
                                        costTotalResource1.getFinanceType().name(),
                                        costTotalResource2.getFinanceType().name()
                                )
                                .withTotal(costTotalResource1.getTotal(), costTotalResource2.getTotal())
                                .buildArray(2, CostTotal.class)
                );
    }

    private FinanceCostTotalResource newApplicationFinanceCostTotalResource(
            Long financeId,
            FinanceRowType type,
            BigDecimal total
    ) {
        return new FinanceCostTotalResource(
                FinanceType.APPLICATION,
                type,
                total,
                financeId
        );
    }
}
