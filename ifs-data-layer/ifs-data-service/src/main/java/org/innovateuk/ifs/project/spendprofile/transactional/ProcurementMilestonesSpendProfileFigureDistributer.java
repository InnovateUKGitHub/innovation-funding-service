package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;


public class ProcurementMilestonesSpendProfileFigureDistributer {

    @Autowired
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    public List<List<Cost>> distributeCosts(Project project) {

        Long durationInMonths = project.getDurationInMonths();
        List<ProjectProcurementMilestoneResource> milestones = projectProcurementMilestoneService.getByProjectId(project.getId()).getSuccess();

        List<Cost> otherCosts = new ArrayList<>();
        List<Cost> vat = new ArrayList<>();

        // TODO get these from repo
        CostCategory otherCostCategory = new CostCategory();
        CostCategory vatCostCategory = new CostCategory();

        IntStream.range(0, durationInMonths.intValue()).forEach(i -> {

            Stream<ProjectProcurementMilestoneResource> milestonesForThisMonth = milestones.stream().filter(m -> m.getMonth().intValue() == i + 1);

            BigInteger totalForMonth = milestonesForThisMonth.map(milestone -> milestone.getPayment()).reduce(BigInteger::add).orElse(BigInteger.ZERO);

            BigInteger vatAmount = totalForMonth.divide(BigInteger.valueOf(6L));
            BigInteger otherCostAmount = totalForMonth.subtract(vatAmount);

            otherCosts.add(new Cost(BigDecimal.valueOf(otherCostAmount.intValue())).
                    withCategory(otherCostCategory).
                    withTimePeriod(i, MONTH, 1, MONTH));
            vat.add(new Cost(BigDecimal.valueOf(vatAmount.intValue())).
                    withCategory(vatCostCategory).
                    withTimePeriod(i, MONTH, 1, MONTH));
        });

        return Arrays.asList(otherCosts, vat);
    }
}
