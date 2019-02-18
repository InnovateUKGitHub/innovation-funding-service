package org.innovateuk.ifs.address.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AddressServiceImplTest extends BaseServiceUnitTest<AddressServiceImpl> {

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private AddressMapper addressMapperMock;

    @Override
    protected AddressServiceImpl supplyServiceUnderTest() {
        return new AddressServiceImpl();
    }

    @Test
    public void testGetById(){

        Address address = newAddress().build();
        AddressResource addressResource = newAddressResource().withId(address.getId()).build();

        when(addressRepositoryMock.findById(address.getId())).thenReturn(Optional.of(address));
        when(addressMapperMock.mapToResource(address)).thenReturn(addressResource);

        ServiceResult<AddressResource> serviceResult = service.getById(addressResource.getId());

        assertTrue(serviceResult.isSuccess());
        assertEquals(addressResource, serviceResult.getSuccess());
    }
}
