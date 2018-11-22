package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MONTHS;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests around the SIL email stub
 */
@RunWith(Parameterized.class)
public class GrantEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<GrantEndpointController> {
    private static final boolean OUTPUT_TEST_JSON = true;
    private static final String OUTPUT_DIRECTORY = "./build/tmp/grant-json";

    protected GrantEndpointController supplyControllerUnderTest() {
        return new GrantEndpointController();
    }

    private Parameter parameter;

    public GrantEndpointControllerMockMvcTest(Parameter parameter) {
        this.parameter = parameter;
    }

    @Test
    public void testSendProject() throws Exception {
        String requestBody = objectMapper.writeValueAsString(parameter.grant());

        if (OUTPUT_TEST_JSON) {
            File outDirectory = new File(OUTPUT_DIRECTORY);
            if (!outDirectory.exists()) {
                assertTrue(outDirectory.mkdir());
            }
            Files.write(Paths.get(OUTPUT_DIRECTORY + "/grant-" + parameter.name() + ".json"), requestBody.getBytes());
        }

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

    @Parameterized.Parameters
    public static Collection<Parameter> parameters() {
        /*
         * Note that we are firing these test grants onto the SIL stub, which at the moment is
         * just testing our JSON serialisation and SIL stub implementation.   This gives us test data.  When
         * SIL end point is available we could fire these tests onto the SIL stub if it proves beneficial.
         */
        return Arrays.asList(
                newParameter(new GrantBuilder().name("basic")),
                newParameter(new GrantBuilder().name("several").withCount(2)),
                newParameter(new GrantBuilder().name("many").withCount(5)),
                newParameter(new GrantBuilder().withSpecialCharacters(true).name("special")),
                newParameter(new GrantBuilder().withLongStrings(true).name("long"))
        );
    }

    private static Parameter newParameter(GrantBuilder grantBuilder) {
        return new Parameter().name(grantBuilder.name()).grant(grantBuilder.build());
    }

    private static class Parameter {
        private Grant grant;
        private String name;

        private Parameter grant(Grant grant) {
            this.grant = grant;
            return this;
        }

        private Grant grant() {
            return grant;
        }


        private Parameter name(String name) {
            this.name = name;
            return this;
        }

        private String name() {
            return name;
        }

    }

}
