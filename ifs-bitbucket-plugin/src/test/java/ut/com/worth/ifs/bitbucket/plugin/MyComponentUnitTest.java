package ut.com.worth.ifs.bitbucket.plugin;

import org.junit.Test;
import com.worth.ifs.bitbucket.plugin.api.MyPluginComponent;
import com.worth.ifs.bitbucket.plugin.impl.MyPluginComponentImpl;

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