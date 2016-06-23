package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import org.junit.Before;
import org.mockito.InjectMocks;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final AssessmentService assessmentService = new AssessmentServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

}