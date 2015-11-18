package com.worth.ifs.application.controller;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.hateoas.Resources;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationControllerBlehTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ApplicationController controller = new ApplicationController();

    @Ignore
    @Test
    public void testBleh(){
        List<ProcessRole> roles = newProcessRole().withApplication(newApplication().build()).build(4);
        User u  = new User();
        when(userRepositoryMock.findOne(1L)).thenReturn(u);
        when(processRoleRepositoryMock.findByUser(u)).thenReturn(roles);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest("get","/bleh")), true);
        Resources<ApplicationResource> result = controller.findByUserId(1L);

        assertEquals(4,result.getContent().size());
    }
}
