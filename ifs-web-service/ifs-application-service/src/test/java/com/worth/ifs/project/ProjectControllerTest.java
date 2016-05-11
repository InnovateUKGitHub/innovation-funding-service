package com.worth.ifs.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;

import com.worth.ifs.BaseControllerMockMVCTest;

public class ProjectControllerTest extends BaseControllerMockMVCTest<ProjectController> {

    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }

    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
        Long projectId = 20L;

        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details"));
    }
}
