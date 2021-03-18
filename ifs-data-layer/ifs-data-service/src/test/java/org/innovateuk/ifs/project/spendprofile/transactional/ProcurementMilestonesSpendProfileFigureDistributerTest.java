package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.project.financechecks.domain.CostTimePeriod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcurementMilestonesSpendProfileFigureDistributerTest {
    @InjectMocks
    private ProcurementMilestonesSpendProfileFigureDistributer distributer;
    @Mock
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    @Test
    public void distributeCosts() {
        Project project = ProjectBuilder.newProject().withDuration(4L).build();

        ProjectProcurementMilestoneResource firstMonthOne = newProjectProcurementMilestoneResource()
                .withMonth(1).withPayment(BigInteger.valueOf(12000L))
                .build();
        ProjectProcurementMilestoneResource firstMonthTwo = newProjectProcurementMilestoneResource()
                .withMonth(1).withPayment(BigInteger.valueOf(24000L))
                .build();
        ProjectProcurementMilestoneResource secondMonth = newProjectProcurementMilestoneResource()
                .withMonth(2).withPayment(BigInteger.valueOf(12000L))
                .build();
        ProjectProcurementMilestoneResource fourthMonth = newProjectProcurementMilestoneResource()
                .withMonth(4).withPayment(BigInteger.valueOf(1200L))
                .build();
        List<ProjectProcurementMilestoneResource> milestones = Arrays.asList(firstMonthOne, firstMonthTwo, secondMonth, fourthMonth);

        when(projectProcurementMilestoneService.getByProjectId(project.getId())).thenReturn(serviceSuccess(milestones));

        List<List<Cost>> result = distributer.distributeCosts(project);

        assertThat(result).hasSize(2);
        List<Cost> otherCosts = result.get(0);
        List<Cost> vat = result.get(1);
        assertThat(otherCosts).hasSize(4);
        assertThat(result.get(1)).hasSize(4);
        assertThat(otherCosts.get(0).getCostTimePeriod().getOffsetAmount()).isEqualTo(0);
        assertThat(otherCosts.get(0).getValue()).isEqualTo(new BigDecimal("30000"));
        assertThat(otherCosts.get(1).getCostTimePeriod().getOffsetAmount()).isEqualTo(1);
        assertThat(otherCosts.get(1).getValue()).isEqualTo(new BigDecimal("10000"));
        assertThat(otherCosts.get(2).getCostTimePeriod().getOffsetAmount()).isEqualTo(2);
        assertThat(otherCosts.get(2).getValue()).isEqualTo(BigDecimal.ZERO);
        assertThat(otherCosts.get(3).getCostTimePeriod().getOffsetAmount()).isEqualTo(3);
        assertThat(otherCosts.get(3).getValue()).isEqualTo(new BigDecimal("1000"));
        assertThat(vat.get(0).getCostTimePeriod().getOffsetAmount()).isEqualTo(0);
        assertThat(vat.get(0).getValue()).isEqualTo(new BigDecimal("6000"));
        assertThat(vat.get(1).getCostTimePeriod().getOffsetAmount()).isEqualTo(1);
        assertThat(vat.get(1).getValue()).isEqualTo(new BigDecimal("2000"));
        assertThat(vat.get(2).getCostTimePeriod().getOffsetAmount()).isEqualTo(2);
        assertThat(vat.get(2).getValue()).isEqualTo(BigDecimal.ZERO);
        assertThat(vat.get(3).getCostTimePeriod().getOffsetAmount()).isEqualTo(3);
        assertThat(vat.get(3).getValue()).isEqualTo(new BigDecimal("200"));
    }
}
