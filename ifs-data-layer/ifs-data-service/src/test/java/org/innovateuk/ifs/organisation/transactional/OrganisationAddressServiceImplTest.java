package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrganisationAddressServiceImplTest extends BaseServiceUnitTest<OrganisationAddressService> {

    @Mock
    private OrganisationAddressRepository organisationAddressRepositoryMock;

    @Mock
    private OrganisationAddressMapper organisationAddressMapperMock;

    @Test
    public void findOne() {

        long id = 1L;

        OrganisationAddress organisationAddress = OrganisationAddressBuilder.newOrganisationAddress().build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationAddressRepositoryMock.findById(id)).thenReturn(Optional.of(organisationAddress));
        when(organisationAddressMapperMock.mapToResource(organisationAddress)).thenReturn(organisationAddressResource);

        ServiceResult<OrganisationAddressResource> result = service.findOne(id);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        verify(organisationAddressRepositoryMock).findById(id);
        verify(organisationAddressMapperMock).mapToResource(organisationAddress);
    }

    @Test
    public void findByOrganisationIdAndAddressId() {

        long organisationId = 1L;
        long addressId = 2L;

        OrganisationAddress organisationAddress = OrganisationAddressBuilder.newOrganisationAddress().build();
        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressId(organisationId, addressId)).thenReturn(organisationAddress);
        when(organisationAddressMapperMock.mapToResource(organisationAddress)).thenReturn(organisationAddressResource);

        ServiceResult<OrganisationAddressResource> result = service.findByOrganisationIdAndAddressId(organisationId, addressId);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        verify(organisationAddressRepositoryMock).findByOrganisationIdAndAddressId(organisationId, addressId);
        verify(organisationAddressMapperMock).mapToResource(organisationAddress);
    }

    @Override
    protected OrganisationAddressService supplyServiceUnderTest() {
        return new OrganisationAddressServiceImpl();
    }
}
