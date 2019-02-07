package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.sil.grant.resource.GrantBuilder.newGrant;
import static org.junit.Assert.assertTrue;

/**
 * Note that this test can write example JSON payloads to file system.   This is useful for sharing with appropriate
 * parties to validate outputs and / or use as inputs.
 */
@RunWith(Parameterized.class)
public class GrantBuilderTest {
    private static final boolean OUTPUT_TEST_JSON = true;
    private static final String OUTPUT_DIRECTORY = "./build/tmp/grant-json";
    private final Parameter parameter;

    public GrantBuilderTest(Parameter parameter) {
        this.parameter = parameter;
    }

    @Test
    public void testGrant() throws IOException {
        String json = new ObjectMapper().writeValueAsString(parameter.grant());
        if (OUTPUT_TEST_JSON) {
            File outDirectory = new File(OUTPUT_DIRECTORY);
            if (!outDirectory.exists()) {
                assertTrue(outDirectory.mkdir());
            }
            Files.write(Paths.get(OUTPUT_DIRECTORY + "/grant-" + parameter.name() + ".json"), json.getBytes());
        }
        assertThat(json, containsString("competitionCode"));
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
                newParameter(newGrant().withCompetitionId(123L).name("competitionid")),
                newParameter(newGrant().name("several").withCount(2)),
                newParameter(newGrant().name("many").withCount(5)),
                newParameter(newGrant().withSpecialCharacters(true).name("special")),
                newParameter(newGrant().withLongStrings(true).name("long")),
                newParameter(newGrant().withSpecialCharacters(true).withLongStrings(true).name("long-special"))
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
