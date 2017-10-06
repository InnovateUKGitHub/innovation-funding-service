package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.transactional.AddressServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.controller.SetupStatusController;
import org.junit.Test;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SetupStatusServiceImplTest extends BaseServiceUnitTest<SetupStatusServiceImpl> {

    @Override
    protected SetupStatusServiceImpl supplyServiceUnderTest() {
        return new SetupStatusServiceImpl();
    }

    @Test
    public void testSave(){

        Address address = newAddress().build();
        AddressResource addressResource = newAddressResource().withId(address.getId()).build();

        when(addressRepositoryMock.findOne(address.getId())).thenReturn(address);
        when(addressMapperMock.mapToResource(address)).thenReturn(addressResource);

        ServiceResult<AddressResource> serviceResult = service.getById(addressResource.getId());

        assertTrue(serviceResult.isSuccess());
        assertEquals(addressResource, serviceResult.getSuccessObject());
    }
}
