package com.worth.ifs.user.builder;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.ProfileAddressResourceBuilder.newProfileAddressResource;
import static org.junit.Assert.assertEquals;

public class ProfileAddressResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        AddressResource expectedAddress = newAddressResource().build();

        ProfileAddressResource profileAddressResource = newProfileAddressResource()
                .withUser(expectedUser)
                .withAddress(expectedAddress)
                .build();

        assertEquals(expectedUser, profileAddressResource.getUser());
        assertEquals(expectedAddress, profileAddressResource.getAddress());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);

        List<ProfileAddressResource>  profileAddressResources = newProfileAddressResource()
                .withUser(expectedUsers)
                .withAddress(expectedAddresses)
                .build(2);

        ProfileAddressResource first = profileAddressResources.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedAddresses[0], first.getAddress());

        ProfileAddressResource second = profileAddressResources.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedAddresses[1], second.getAddress());
    }
}
