package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled("TODO")
@ExtendWith(MockitoExtension.class)
class StubUidSupplierTest {

    @Mock
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    private StubUidSupplier stubUidSupplier = new StubUidSupplier();

    @Test
    void testUnset() {
        assertThrows(NullPointerException.class, () -> stubUidSupplier.getUid(null));
    }

    @Test
    void testSetRead() throws IOException {
        String defaultUuid = UUID.randomUUID().toString();
        Mockito.lenient().when(stubDevConfigurationProperties.getDefaultUuid()).thenReturn(defaultUuid);
        stubUidSupplier.init();

        String nextUuid = UUID.randomUUID().toString();
        stubUidSupplier.setUuid(nextUuid);
        assertThat(stubUidSupplier.getUid(null), equalTo(nextUuid));
    }
}