package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.NONE;
import static org.innovateuk.ifs.util.CollectionFunctions.negate;
import static org.junit.Assert.assertNotNull;

public class ActivityLogMessageTest extends BaseIntegrationTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    public void url() {
        stream(ActivityType.values())
                .filter(negate(NONE::equals))
                .forEach((type) -> {
                    assertNotNull(messageSource.getMessage(format("ifs.activity.log.%s.title", type.name()),
                            new Object[]{""},
                            Locale.getDefault())
                    );
                    assertNotNull(messageSource.getMessage(format("ifs.activity.log.%s.link", type.name()),
                            new Object[]{"", ""},
                            Locale.getDefault())
                    );
                });
    }
}
