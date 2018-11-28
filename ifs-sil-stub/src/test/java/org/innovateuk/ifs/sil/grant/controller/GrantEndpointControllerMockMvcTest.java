package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.GrantBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.sil.grant.resource.GrantBuilder.newGrant;
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
                newParameter(newGrant().name("basic")),
                newParameter(newGrant().name("several").withCount(2)),
                newParameter(newGrant().name("many").withCount(5)),
                newParameter(newGrant().withSpecialCharacters(true).name("special")),
                newParameter(newGrant().withLongStrings(true).name("long"))
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
