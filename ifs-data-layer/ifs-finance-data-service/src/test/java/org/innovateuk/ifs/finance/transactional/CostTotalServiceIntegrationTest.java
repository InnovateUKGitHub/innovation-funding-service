package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.repository.CostTotalRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.CostTotalBuilder.newCostTotal;

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
        FinanceCostTotalResource costTotalResource = new FinanceCostTotalResource();
        costTotalResource.setFinanceId(10L);
        costTotalResource.setName(FinanceRowType.LABOUR.getName());
        costTotalResource.setFinanceType("APPLICATION");
        costTotalResource.setTotal(BigDecimal.valueOf(5000L));

        ServiceResult<Void> result = costTotalService.saveCostTotal(costTotalResource);

        assertThat(result.isSuccess()).isTrue();

        assertThat(costTotalRepository.count()).isOne();

        CostTotal savedCostTotal = costTotalRepository.findByFinanceId(costTotalResource.getFinanceId());

        assertThat(savedCostTotal)
                .isEqualToComparingOnlyGivenFields(
                        newCostTotal()
                                .withFinanceId(costTotalResource.getFinanceId())
                                .withName(costTotalResource.getName())
                                .withType(costTotalResource.getFinanceType())
                                .withTotal(costTotalResource.getTotal())
                                .build()
                );
    }
}
