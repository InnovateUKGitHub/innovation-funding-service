package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTest {

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        BuilderAmendFunctions.clearUniqueIds();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller, new ErrorControllerAdvice())
                .setHandlerExceptionResolvers(createExceptionResolver())
                .setViewResolvers(viewResolver())
                .build();
    }

    protected void setLoggedInUserAuthentication(UserAuthentication user) {
        SecurityContextHolder.getContext().setAuthentication(user);
    }

    /**
     * Get the user on the Spring Security ThreadLocals
     */
    protected User getLoggedInUser() {
        return ((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails();
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    protected void setLoggedInUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }
}