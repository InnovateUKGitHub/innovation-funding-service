package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.mapper.ApplicationProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ApplicationProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestone;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneResourceBuilder.newApplicationProcurementMilestoneResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationProcurementMilestoneServiceImplTest {

    @InjectMocks
    private ApplicationProcurementMilestoneServiceImpl service;

    @Mock
    private ApplicationProcurementMilestoneRepository repository;

    @Mock
    private ApplicationProcurementMilestoneMapper mapper;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Test
    public void getByApplicationIdAndOrganisationId() {
        long applicationId = 1L;
        long organisationId = 2L;

        List<ApplicationProcurementMilestone> domains = newArrayList(new ApplicationProcurementMilestone());
        List<ApplicationProcurementMilestoneResource> resources = newApplicationProcurementMilestoneResource().build(1);

        when(repository.findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationIdOrderByMonthAsc(applicationId, organisationId))
                .thenReturn(domains);
        when(mapper.mapToResource(domains.get(0))).thenReturn(resources.get(0));

        ServiceResult<List<ApplicationProcurementMilestoneResource>> result = service.getByApplicationIdAndOrganisationId(applicationId, organisationId);

        assertThat(result.getSuccess(), is(equalTo(resources)));
    }

    @Test
    public void findMaxMilestoneMonth() {
        long applicationId = 1L;
        Application application = newApplication()
                .withApplicationFinancesList(asList(
                        newApplicationFinance().withMilestones(asList(
                                newApplicationProcurementMilestone().withMonth(2).build(),
                                newApplicationProcurementMilestone().withMonth(4).build(),
                                newApplicationProcurementMilestone().withMonth(12).build())
                        ).build())
                ).build();

        when(applicationRepository.findById(applicationId)).thenReturn(of(application));

        ServiceResult<Optional<Integer>> result = service.findMaxMilestoneMonth(applicationId);

        assertThat(result.getSuccess(), is(equalTo(of(12))));
    }

    @Test
    public void newDomain() {
        long applicationId = 1L;
        long organisationId = 2L;

        ApplicationProcurementMilestoneResource resource = newApplicationProcurementMilestoneResource()
                .withApplicationId(applicationId)
                .withOrganisationId(organisationId)
                .build();
        ApplicationFinance finance = newApplicationFinance().build();

        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(of(finance));

        ServiceResult<ApplicationProcurementMilestone> result = service.newDomain(resource);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getSuccess().getApplicationFinance(), is(finance));
    }

    @Test
    public void getRepository() {
        assertThat(service.getRepository(), is(repository));
    }

}