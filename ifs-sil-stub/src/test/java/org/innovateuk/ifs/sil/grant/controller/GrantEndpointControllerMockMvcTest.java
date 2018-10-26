package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Project;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MONTHS;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests around the SIL email stub
 */
public class GrantEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<GrantEndpointController> {

    protected GrantEndpointController supplyControllerUnderTest() {
        return new GrantEndpointController();
    }

    private Project createProject() {
        Project project = new Project();
        Set<Participant> participants = new HashSet<>(Arrays.asList(createParticipant(), createParticipant()));
        project.setParticipant(participants);
        return project;
    }

    private Participant createParticipant() {
        Participant participant = new Participant();
        participant.setForecasts(createForecasts(LocalDate.of(2018, 10,1 ),12, 50_000));
        return participant;
    }

    private Set<Forecast> createForecasts(LocalDate start, int durationInMonths, double total) {
        double value = total / durationInMonths;
        return Stream.iterate(0L, i -> i + 1).limit(durationInMonths)
                .map(i -> new Forecast().start(start).end(start.plus(i + 1, MONTHS)).value(value))
                .collect(Collectors.toSet());
    }

    @Test
    public void testSendProject() throws Exception {
        String requestBody = objectMapper.writeValueAsString(createProject());

        mockMvc.
                perform(
                        post("/silstub/sendproject").
                                header("Content-Type", "application/json").
                                content(requestBody)
                ).
                andExpect(status().isAccepted()).
                andDo(document("silstub/sendproject",
                        requestHeaders(
                                headerWithName("Content-Type").description("Needs to be application/json")
                        )
                ));
    }
}
