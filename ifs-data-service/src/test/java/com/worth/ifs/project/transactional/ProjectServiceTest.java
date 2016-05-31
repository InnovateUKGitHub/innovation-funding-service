package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.InjectMocks;

public class ProjectServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @InjectMocks
    private ProjectService projectService = new ProjectServiceImpl();

    public void controllerCanCreateNewProject(){

    }
}
