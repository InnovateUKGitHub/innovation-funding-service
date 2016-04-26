package com.worth.ifs;

import org.junit.Test;

import static com.worth.ifs.config.EnhancedUtextProcessor.escape;
import junit.framework.Assert;

public class EscapeTest {
    @Test
    public void testCorrectEscape(){
        String b1 = "<script>test</script>";
        String b2 = "< script>test</script>";
        String b3 = "< script >test</script>";
        String b4 = "<script>test< /script>";
        String b5 = "<script>test< / script>";
        String b6 = "<script>test</script >";
        String b7 = "<SCript attr=abc>test< /script attr=abc>";

        String a1 = "&lt;script&gt;test&lt;/script&gt;";
        String a2 = "&lt; script&gt;test&lt;/script&gt;";
        String a3 = "&lt; script &gt;test&lt;/script&gt;";
        String a4 = "&lt;script&gt;test&lt; /script&gt;";
        String a5 = "&lt;script&gt;test&lt; / script&gt;";
        String a6 = "&lt;script&gt;test&lt;/script &gt;";
        String a7 = "&lt;SCript attr=abc&gt;test&lt; /script attr=abc&gt;";

        Assert.assertEquals(escape(b1),a1);
        Assert.assertEquals(escape(b2),a2);
        Assert.assertEquals(escape(b3),a3);
        Assert.assertEquals(escape(b4),a4);
        Assert.assertEquals(escape(b5),a5);
        Assert.assertEquals(escape(b6),a6);
        Assert.assertEquals(escape(b7),a7);
    }

}
