package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AddressServiceImplTest extends BaseServiceUnitTest<AddressService> {

    @Mock
    private AddressRestService addressRestService;

    @Override
    protected AddressService supplyServiceUnderTest() {
        return new AddressServiceImpl();
    }

    @Test
    public void testGetById() throws Exception {
        Long addressId = 3L;
        AddressResource address = new AddressResource();
        when(addressRestService.getById(addressId)).thenReturn(restSuccess(address));

        ServiceResult<AddressResource> serviceResult = service.getById(addressId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(address, serviceResult.getSuccessObjectOrThrowException());
    }

}