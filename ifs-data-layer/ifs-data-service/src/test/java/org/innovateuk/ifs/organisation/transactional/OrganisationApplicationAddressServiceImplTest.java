package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder;
import org.innovateuk.ifs.organisation.domain.OrganisationApplicationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationApplicationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationApplicationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrganisationApplicationAddressServiceImplTest extends BaseServiceUnitTest<OrganisationApplicationAddressService> {

    @Mock
    private OrganisationApplicationAddressRepository organisationApplicationAddressRepositoryMock;

    @Mock
    private OrganisationApplicationAddressMapper organisationApplicationAddressMapperMock;

    @Test
    public void findOne() {

        long id = 1L;

        OrganisationApplicationAddress organisationApplicationAddress = OrganisationAddressBuilder.newOrganisationAddress().build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationApplicationAddressRepositoryMock.findById(id)).thenReturn(Optional.of(organisationApplicationAddress));
        when(organisationApplicationAddressMapperMock.mapToResource(organisationApplicationAddress)).thenReturn(organisationAddressResource);

        ServiceResult<OrganisationAddressResource> result = service.findOne(id);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        verify(organisationApplicationAddressRepositoryMock).findById(id);
        verify(organisationApplicationAddressMapperMock).mapToResource(organisationApplicationAddress);
    }

    @Test
    public void findByOrganisationIdAndAddressId() {

        long organisationId = 1L;
        long addressId = 2L;

        OrganisationApplicationAddress organisationApplicationAddress = OrganisationAddressBuilder.newOrganisationAddress().build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationApplicationAddressRepositoryMock.findByOrganisationIdAndAddressId(organisationId, addressId)).thenReturn(organisationApplicationAddress);
        when(organisationApplicationAddressMapperMock.mapToResource(organisationApplicationAddress)).thenReturn(organisationAddressResource);

        ServiceResult<OrganisationAddressResource> result = service.findByOrganisationIdAndAddressId(organisationId, addressId);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        verify(organisationApplicationAddressRepositoryMock).findByOrganisationIdAndAddressId(organisationId, addressId);
        verify(organisationApplicationAddressMapperMock).mapToResource(organisationApplicationAddress);
    }

    @Override
    protected OrganisationApplicationAddressService supplyServiceUnderTest() {
        return new OrganisationApplicationAddressServiceImpl();
    }
}
