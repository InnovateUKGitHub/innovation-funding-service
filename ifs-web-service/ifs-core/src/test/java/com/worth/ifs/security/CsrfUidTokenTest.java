package com.worth.ifs.security;

import com.worth.ifs.security.CsrfTokenService.CsrfUidToken;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.web.csrf.CsrfException;

import java.time.Instant;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CsrfUidTokenTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_parse() throws Exception {
        final String nonce = "add3fc58-0c7c-410d-9223-15a1153dcd8b";
        final String uId = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
        final String timestampAsString = "2016-04-20T14:23:47.241Z";

        final String token = asList(nonce, uId, timestampAsString).stream().collect(Collectors.joining("_"));
        CsrfUidToken actual = CsrfUidToken.parse(token);

        assertEquals(nonce, actual.getNonce());
        assertEquals(uId, actual.getuId());
        assertEquals(Instant.parse(timestampAsString), actual.getTimestamp());
    }

    @Test
    public void test_parse_malformed() throws Exception {
        final String valid = "add3fc58-0c7c-410d-9223-15a1153dcd8b_5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de_2016-04-20T14:23:47.241Z";
        // remove the date to create a malformed token
        final String malformed = valid.substring(0, valid.lastIndexOf("_"));
        thrown.expect(CsrfException.class);
        thrown.expectMessage(format("Could not parse CSRF token into constituent elements '%s'", malformed));
        CsrfUidToken.parse(malformed);
    }

    @Test
    public void test_parse_empty() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("Could not parse blank CSRF token.");
        CsrfUidToken.parse("");
    }

    @Test
    public void test_parse_null() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("Could not parse blank CSRF token.");
        CsrfUidToken.parse(null);
    }
}