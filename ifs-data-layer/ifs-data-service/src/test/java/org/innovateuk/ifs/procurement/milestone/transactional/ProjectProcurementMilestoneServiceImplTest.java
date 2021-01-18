package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.mapper.ProjectProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ProjectProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectProcurementMilestoneServiceImplTest {

    @InjectMocks
    private ProjectProcurementMilestoneServiceImpl service;

    @Mock
    private ProjectProcurementMilestoneRepository repository;

    @Mock
    private ProjectProcurementMilestoneMapper mapper;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Test
    public void getByProjectIdAndOrganisationId() {
        long projectId = 1L;
        long organisationId = 2L;

        List<ProjectProcurementMilestone> domains = newArrayList(new ProjectProcurementMilestone());
        List<PaymentMilestoneResource> resources = newProjectProcurementMilestoneResource().build(1);

        when(repository.findByProjectFinanceProjectIdAndProjectFinanceOrganisationIdOrderByMonthAsc(projectId, organisationId))
                .thenReturn(domains);
        when(mapper.mapToResource(domains.get(0))).thenReturn(resources.get(0));

        ServiceResult<List<PaymentMilestoneResource>> result = service.getByProjectIdAndOrganisationId(projectId, organisationId);

        assertThat(result.getSuccess(), is(equalTo(resources)));
    }

    @Test
    public void getByProjectId() {
        long projectId = 1L;

        List<ProjectProcurementMilestone> domains = newArrayList(new ProjectProcurementMilestone());
        List<PaymentMilestoneResource> resources = newProjectProcurementMilestoneResource().build(1);

        when(repository.findByProjectFinanceProjectId(projectId))
                .thenReturn(domains);
        when(mapper.mapToResource(domains.get(0))).thenReturn(resources.get(0));

        ServiceResult<List<PaymentMilestoneResource>> result = service.getByProjectId(projectId);

        assertThat(result.getSuccess(), is(equalTo(resources)));
    }

    @Test
    public void newDomain() {
        long projectId = 1L;
        long organisationId = 2L;

        PaymentMilestoneResource resource = newProjectProcurementMilestoneResource()
                .withProjectId(projectId)
                .withOrganisationId(organisationId)
                .build();
        ProjectFinance finance = newProjectFinance().build();

        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(finance));

        ServiceResult<ProjectProcurementMilestone> result = service.newDomain(resource);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getSuccess().getProjectFinance(), is(finance));
    }

    @Test
    public void getRepository() {
        assertThat(service.getRepository(), is(repository));
    }

}