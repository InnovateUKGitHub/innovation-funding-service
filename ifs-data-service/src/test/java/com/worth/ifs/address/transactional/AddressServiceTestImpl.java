package com.worth.ifs.address.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;

import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AddressServiceTestImpl extends BaseServiceUnitTest<AddressService>{

    @Override
    protected AddressService supplyServiceUnderTest() {
        return new AddressServiceImpl();
    }

    @Test
    public void testGetById(){
        Address address = newAddress().build();
        AddressResource addressResource = newAddressResource().withId(address.getId()).build();
        when(addressMapperMock.mapToResource(address)).thenReturn(addressResource);
        ServiceResult<AddressResource> serviceResult = service.getById(addressResource.getId());
        assertTrue(serviceResult.isSuccess());
    }
}
