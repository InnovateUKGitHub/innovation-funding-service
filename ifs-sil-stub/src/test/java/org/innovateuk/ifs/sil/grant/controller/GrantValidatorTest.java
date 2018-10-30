package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GrantValidatorTest {
    @Test
    public void assertErrorsGenerated() {
        Grant grant = new Grant();
        List<String> errors = new GrantValidator().checkForErrors(grant);
        assertFalse(errors.isEmpty());
        assertEquals("participants is null", errors.get(0));
    }
}
