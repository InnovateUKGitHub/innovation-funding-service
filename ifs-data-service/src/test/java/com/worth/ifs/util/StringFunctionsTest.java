package com.worth.ifs.util;

import org.junit.Test;

import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static org.junit.Assert.assertEquals;

public class StringFunctionsTest {

    @Test
    public void countWords() throws Exception {
        assertEquals(15, StringFunctions.countWords(join(" ", nCopies(15, "content"))));
    }

    @Test
    public void countWords_valueWithHtml() throws Exception {
        assertEquals(15, StringFunctions.countWords("<td><p style=\"font-variant: small-caps\">This value is made up of fifteen words even though it is wrapped within HTML.</p></td>"));
    }

    @Test
    public void countWords_noContent() throws Exception {
        assertEquals(0, StringFunctions.countWords(null));
    }

    @Test
    public void countWords_emptyContent() throws Exception {
        assertEquals(0, StringFunctions.countWords(""));
    }
}