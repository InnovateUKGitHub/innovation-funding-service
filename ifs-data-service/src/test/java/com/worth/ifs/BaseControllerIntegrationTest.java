package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

/**
 * This this the base class for Controller integration tests.  Subclasses will have access to a Controller and be able
 * to test it against a full stack of real REST services, transactional Services and Repositories.
 *
 * Created by dwatson on 02/10/15.
 */
@Transactional
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseWebIntegrationTest {
    public Log LOG = LogFactory.getLog(getClass());

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);
}
