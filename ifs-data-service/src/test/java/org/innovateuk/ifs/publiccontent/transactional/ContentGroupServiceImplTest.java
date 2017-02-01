package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;

public class ContentGroupServiceImplTest extends BaseServiceUnitTest<ContentGroupServiceImpl> {

    @Override
    protected ContentGroupServiceImpl supplyServiceUnderTest() {
        return new ContentGroupServiceImpl();
    }
}
