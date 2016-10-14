package com.worth.ifs.user.builder;

import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileContractResource;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static org.junit.Assert.assertEquals;


public class ProfileContractResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        ContractResource expectedContract = newContractResource().build();
        boolean expectedCurrentAgreement = true;
        LocalDateTime expectedContractSignedDate = LocalDateTime.now();

        ProfileContractResource profileContractResource = newProfileContractResource()
                .withUser(expectedUser)
                .withContract(expectedContract)
                .withCurrentAgreement(expectedCurrentAgreement)
                .withContractSignedDate(expectedContractSignedDate)
                .build();

        assertEquals(expectedUser, profileContractResource.getUser());
        assertEquals(expectedContract, profileContractResource.getContract());
        assertEquals(expectedCurrentAgreement, profileContractResource.isCurrentAgreement());
        assertEquals(expectedContractSignedDate, profileContractResource.getContractSignedDate());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        ContractResource[] expectedContracts = newContractResource().buildArray(2, ContractResource.class);
        Boolean[] expectedCurrentAgreements = {true, false};
        LocalDateTime[] expectedContractSignedDates = {LocalDateTime.now(), LocalDateTime.now()};

        List<ProfileContractResource> profileContractResources = newProfileContractResource()
                .withUser(expectedUsers)
                .withContract(expectedContracts)
                .withCurrentAgreement(expectedCurrentAgreements)
                        .withContractSignedDate(expectedContractSignedDates)
                        .build(2);

        ProfileContractResource first = profileContractResources.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedContracts[0], first.getContract());
        assertEquals(expectedCurrentAgreements[0], first.isCurrentAgreement());
        assertEquals(expectedContractSignedDates[0], first.getContractSignedDate());

        ProfileContractResource second = profileContractResources.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedContracts[1], second.getContract());
        assertEquals(expectedCurrentAgreements[1], second.isCurrentAgreement());
        assertEquals(expectedContractSignedDates[1], second.getContractSignedDate());
    }

}