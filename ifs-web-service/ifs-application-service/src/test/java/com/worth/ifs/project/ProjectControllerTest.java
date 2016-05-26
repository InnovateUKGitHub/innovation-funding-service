package com.worth.ifs.project;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;

public class ProjectControllerTest extends BaseControllerMockMVCTest<ProjectController> {
	
    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }
    
    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
    	Long projectId = 20L;

    	ApplicationResource application = newApplicationResource().build();
    	when(applicationService.getById(projectId)).thenReturn(application);

        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", application))
                .andExpect(view().name("project/details"));
    }
}
