package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * TODO DW - document this class
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantSaverTest {

    @InjectMocks
    private GrantSaver grantSaver;

    @Mock
    private EuGrantService euGrantServiceMock;

    @Test
    public void saveGrant() {

        grantSaver.saveGrant(null);
    }
}
