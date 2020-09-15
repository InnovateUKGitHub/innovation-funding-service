package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.innovateuk.ifs.application.repository.ApplicationOrganisationAddressRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ApplicationOrganisationAddressServiceImplTest extends BaseServiceUnitTest<ApplicationOrganisationAddressServiceImpl> {
    @Override
    protected ApplicationOrganisationAddressServiceImpl supplyServiceUnderTest() {
        return new ApplicationOrganisationAddressServiceImpl();
    }

    @Mock
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Test
    public void getAddress() {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;

        Address address = newAddress().build();

        ApplicationOrganisationAddress applicationOrganisationAddress = new ApplicationOrganisationAddress(
                newOrganisationAddress().withAddress(address).build(),
                newApplication().build()
        );

        AddressResource addressResource = newAddressResource().build();

        when(applicationOrganisationAddressRepository.findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(applicationId, organisationId, type.getId()))
                .thenReturn(Optional.of(applicationOrganisationAddress));
        when(addressMapper.mapToResource(address)).thenReturn(addressResource);

        ServiceResult<AddressResource> result = service.getAddress(applicationId, organisationId, type);

        assertEquals(result.getSuccess(), addressResource);
    }

    @Test
    public void updateAddress() {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;

        AddressResource addressResource = newAddressResource().build();

        Address address = mock(Address.class);

        ApplicationOrganisationAddress applicationOrganisationAddress = new ApplicationOrganisationAddress(
                newOrganisationAddress().withAddress(address).build(),
                newApplication().build()
        );

        when(applicationOrganisationAddressRepository.findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(applicationId, organisationId, type.getId()))
                .thenReturn(Optional.of(applicationOrganisationAddress));
        when(addressMapper.mapToResource(address)).thenReturn(addressResource);

        ServiceResult<AddressResource> result = service.updateAddress(applicationId, organisationId, type, addressResource);

        assertEquals(result.getSuccess(), addressResource);
        verify(address).copyFrom(addressResource);
    }


}