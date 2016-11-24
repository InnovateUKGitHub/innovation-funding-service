package com.worth.ifs.project.status.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionProjectsStatusControllerTest extends BaseControllerMockMVCTest<CompetitionProjectsStatusController> {

    @Test
    public void testViewCompetitionStatusPage() throws Exception {
        Long competitionId = 123L;

        CompetitionProjectsStatusResource competitionProjectsStatus = newCompetitionProjectsStatusResource().build();

        when(projectStatusServiceMock.getCompetitionStatus(competitionId)).thenReturn(competitionProjectsStatus);

        mockMvc.perform(get("/competition/" + competitionId + "/status"))
                .andExpect(view().name("project/competition-status"))
                .andExpect(model().attribute("model", any(CompetitionProjectStatusViewModel.class)))
                .andReturn();
    }

    @Test
    public void exportBankDetails() throws Exception {

        Long competitionId = 123L;

        ByteArrayResource result = new ByteArrayResource("My content!".getBytes());

        when(bankDetailsService.downloadByCompetition(competitionId)).thenReturn(result);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

        mockMvc.perform(get("/competition/123/status/bank-details/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("text/csv")))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-disposition", "attachment;filename=" + String.format("Bank_details_%s_%s.csv", competitionId, LocalDateTime.now().format(formatter))))
                .andExpect(content().string("My content!"));

        verify(bankDetailsService).downloadByCompetition(123L);
    }

    @Override
    protected CompetitionProjectsStatusController supplyControllerUnderTest() {
        return new CompetitionProjectsStatusController();
    }
}
