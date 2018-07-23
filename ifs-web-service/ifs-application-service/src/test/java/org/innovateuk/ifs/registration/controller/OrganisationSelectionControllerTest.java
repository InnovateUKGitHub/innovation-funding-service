package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationSelectionControllerTest extends BaseControllerMockMVCTest<OrganisationSelectionController> {







    @Override
    protected OrganisationSelectionController supplyControllerUnderTest() {
        return new OrganisationSelectionController();
    }
}