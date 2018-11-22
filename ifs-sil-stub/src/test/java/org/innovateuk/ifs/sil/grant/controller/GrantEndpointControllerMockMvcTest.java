package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Period;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    private Grant createGrant() {
        Grant project = new Grant();
        Set<Participant> participants = new HashSet<>(Arrays.asList(createParticipant(), createParticipant()));
        project.setParticipants(participants);
        return project;
    }

    private Participant createParticipant() {
        Participant participant = new Participant();
        participant.setForecasts(createForecasts(Arrays.asList("Overheads", "Other"),12,
                BigDecimal.valueOf(50_000)));
        return participant;
    }

    private Set<Forecast> createForecasts(List<String> costCategories,
                                          int durationInMonths, BigDecimal total) {
        BigDecimal value = total.divide(BigDecimal.valueOf(durationInMonths * costCategories.size()),
                6, BigDecimal.ROUND_UP);
        return costCategories.stream().map(category ->
                new Forecast().costCategory(category).periods(createPeriods(durationInMonths, value)))
                .collect(Collectors.toSet());
    }

    private Set<Period> createPeriods(int durationInMonths, BigDecimal value) {
        return Stream.iterate(0, i -> i + 1).limit(durationInMonths)
                .map(i -> new Period().month(i + 1).value(value.longValue()))
                .collect(Collectors.toSet());
    }

    @Test
    public void testSendProject() throws Exception {
        String requestBody = objectMapper.writeValueAsString(createGrant());

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
