package org.innovateuk.ifs.starters.newrelic;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = {NewRelicEventChannel.class})
class NewRelicEventChannelTest {

    @Autowired
    private NewRelicEventChannel newRelicEventChannel;

    @Test
    void testNewRelicEventChannel() {
        assertDoesNotThrow(() ->
            newRelicEventChannel.sendErrorEvent("TEST", new RuntimeException(), this.getClass(), ImmutableMap.of("test", "test"))
        );
    }

}