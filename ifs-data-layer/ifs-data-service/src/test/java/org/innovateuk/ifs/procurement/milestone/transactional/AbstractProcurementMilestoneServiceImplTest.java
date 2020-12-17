package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.mapper.ProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractProcurementMilestoneServiceImplTest {

    @InjectMocks
    private ServiceClazz service = new ServiceClazz();

    @Mock
    private RepositoryClazz repository;

    @Mock
    private MapperClazz mapper;

    @Test
    public void create() {
        ResourceClazz resource = new ResourceClazz();
        resource.setDeliverable("deliverable");
        resource.setMonth(1);
        resource.setPayment(BigInteger.ONE);

        when(repository.save(any(DomainClazz.class))).thenAnswer((inv) -> inv.getArgument(0));
        when(mapper.mapToResource(any(DomainClazz.class))).thenReturn(resource);

        ServiceResult<ResourceClazz> result = service.create(resource);

        verify(repository).save(argThat(lambdaMatches(domain -> domain.getDeliverable().equals(resource.getDeliverable())
        && domain.getMonth().equals(resource.getMonth())
        && domain.getPayment().equals(resource.getPayment()))));

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getSuccess(), is(resource));
    }

    @Test
    public void update() {
        ResourceClazz resource = new ResourceClazz();
        resource.setId(1L);
        resource.setDeliverable("deliverable");
        resource.setMonth(1);
        resource.setPayment(BigInteger.ONE);

        DomainClazz domain = new DomainClazz();

        when(repository.findById(resource.getId())).thenReturn(Optional.of(domain));
        when(repository.save(domain)).thenReturn(domain);
        when(mapper.mapToResource(any(DomainClazz.class))).thenReturn(resource);

        ServiceResult<ResourceClazz> result = service.update(resource);

        verify(repository).save(domain);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getSuccess(), is(resource));
        assertThat(domain.getDeliverable(), is(equalTo(resource.getDeliverable())));
        assertThat(domain.getMonth(), is(equalTo(resource.getMonth())));
        assertThat(domain.getPayment(), is(equalTo(resource.getPayment())));
    }

    @Test
    public void delete() {
        long milestoneId = 1L;

        ServiceResult<Void> result = service.delete(IdClazz.of(milestoneId));

        assertThat(result.isSuccess(), is(true));
        verify(repository).deleteById(milestoneId);
    }

    @Test
    public void get() {
        long milestoneId = 1L;
        ResourceClazz resource = new ResourceClazz();
        DomainClazz domain = new DomainClazz();

        when(repository.findById(milestoneId)).thenReturn(Optional.of(domain));
        when(mapper.mapToResource(any(DomainClazz.class))).thenReturn(resource);

        ServiceResult<ResourceClazz> result = service.get(IdClazz.of(milestoneId));

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getSuccess(), is(resource));
    }

    private class ResourceClazz extends ProcurementMilestoneResource {}

    private class DomainClazz extends ProcurementMilestone {}

    private interface RepositoryClazz extends ProcurementMilestoneRepository<DomainClazz> {}

    private abstract class MapperClazz extends ProcurementMilestoneMapper<DomainClazz, ResourceClazz> {}

    private static class IdClazz extends ProcurementMilestoneId {
        public static IdClazz of(long id) {
            IdClazz idClazz = new IdClazz();
            idClazz.setId(id);
            return idClazz;
        }
    };

    private class ServiceClazz extends AbstractProcurementMilestoneServiceImpl<ResourceClazz, DomainClazz, IdClazz> {
        @Override
        protected ServiceResult<DomainClazz> newDomain(ResourceClazz resource) {
            return serviceSuccess(new DomainClazz());
        }

        @Override
        protected ProcurementMilestoneRepository<DomainClazz> getRepository() {
            return repository;
        }
    };
}