package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationControllerTest extends BaseControllerMockMVCTest<OrganisationController> {

    @Override
    protected OrganisationController supplyControllerUnderTest() {
        return new OrganisationController();
    }

    @Test
    public void findByIdShouldReturnOrganisation() throws Exception {
        when(organisationRepositoryMock.findOne(1L)).thenReturn(newOrganisation().withId(1L).withName("uniqueOrganisationName").build());

        mockMvc.perform(get("/organisation/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("uniqueOrganisationName")));
    }
}