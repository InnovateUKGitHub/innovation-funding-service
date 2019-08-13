package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.runner.RunWith;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class ApplicationSectionControllerTest extends BaseControllerMockMVCTest<ApplicationSectionController> {
    @Override
    protected ApplicationSectionController supplyControllerUnderTest() {
        return new ApplicationSectionController();
    }
}