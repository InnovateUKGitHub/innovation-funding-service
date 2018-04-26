package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.junit.Assert.assertEquals;

@Rollback
public class SectionControllerIntegrationTest extends BaseControllerIntegrationTest<SectionController> {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private QuestionMapper questionMapper;

    private Long sectionId;

    @Before
    public void setUp() throws Exception {
        sectionId = 1L;
        addBasicSecurityUser();
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(SectionController controller) {
        this.controller = controller;
    }

    @Test
    public void testGetById() throws Exception {
        SectionResource section = controller.getById(sectionId).getSuccess();
        assertEquals("Project details", section.getName());

        section = controller.getById(2L).getSuccess();
        assertEquals("Application questions", section.getName());
    }
}
