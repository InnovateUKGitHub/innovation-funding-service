package ut.org.innovateuk.ifs.bitbucket.plugin;

import org.junit.Test;
import org.innovateuk.ifs.bitbucket.plugin.api.MyPluginComponent;
import org.innovateuk.ifs.bitbucket.plugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}
