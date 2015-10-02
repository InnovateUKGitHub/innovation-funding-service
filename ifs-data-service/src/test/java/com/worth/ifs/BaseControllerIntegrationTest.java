package com.worth.ifs;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseWebIntegrationTest {

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);
}
