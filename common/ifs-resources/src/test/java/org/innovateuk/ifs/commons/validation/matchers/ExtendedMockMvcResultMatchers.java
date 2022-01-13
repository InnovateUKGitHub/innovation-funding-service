package org.innovateuk.ifs.commons.validation.matchers;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
 * Serves the custom @{ExtendedModelResultMatchers} which contains extra assertions we commonly need.
 */

public class ExtendedMockMvcResultMatchers extends MockMvcResultMatchers {
    public static ExtendedModelResultMatchers model() {
        return new ExtendedModelResultMatchers();
    }
}
