package com.worth.ifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dwatson on 02/10/15.
 */
@Transactional
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseWebIntegrationTest {

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);
}
