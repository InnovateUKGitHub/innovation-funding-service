package org.innovateuk.ifs.util;

import org.junit.Test;

import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Test
    public void stripHtml() throws Exception {
        assertEquals("This value is made up of: a table cell a paragraph and a list.", StringFunctions.stripHtml(
                "<tr>" +
                        "    <td>" +
                        "        <p style=\"font-variant: small-caps\">This value is made up of:</p>" +
                        "    </td>" +
                        "    <td>" +
                        "        <ul>" +
                        "            <li>a table cell</li>" +
                        "            <li>a paragraph</li>" +
                        "            <li>and a list.</li>" +
                        "        </ul>" +
                        "    </td>" +
                        "</tr>"));
    }

    @Test
    public void stripHtml_noHtml() throws Exception {
        String content = "This value does not contain HTML markup.\n It should remain untouched.";
        assertEquals(content, StringFunctions.stripHtml(content));
    }

    @Test
    public void stripHtml_noContent() throws Exception {
        assertNull(StringFunctions.stripHtml(null));
    }

    @Test
    public void stripHtml_emptyContent() throws Exception {
        assertEquals("", StringFunctions.stripHtml(""));
    }

    @Test
    public void plainTextToHtml() throws Exception {
        assertEquals("This text is split<br/>over several<br/>lines.<br/><br/>It contains characters &lt; &gt; &amp; &quot; which need escaping.",
                StringFunctions.plainTextToHtml("This text is split\nover several\nlines.\n\nIt contains characters < > & \" which need escaping."));
    }

    @Test
    public void plainTextToHtml_newlines() throws Exception {
        String content = "Line 1\nLine 2\fLine 3\r\nLine 4\rLine 5";
        assertEquals("Line 1<br/>Line 2<br/>Line 3<br/>Line 4<br/>Line 5", StringFunctions.plainTextToHtml(content));
    }

    @Test
    public void plainTextToHtml_noContent() throws Exception {
        assertNull(StringFunctions.plainTextToHtml(null));
    }

    @Test
    public void plainTextToHtml_emptyContent() throws Exception {
        assertEquals("", StringFunctions.plainTextToHtml(""));
    }
}
