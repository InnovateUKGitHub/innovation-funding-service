package org.innovateuk.ifs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base test class that pre-configures MockMVC for use in controller tests.
 *
 * @param <ControllerType>
 */
abstract public class MockMvcTest<ControllerType> {

    protected MockMvc mockMvc;

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    @Autowired
    protected ObjectMapper objectMapper;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    public abstract ControllerType supplyControllerUnderTest();

    @Before
    public void setupMockMvcTest() {
        mockMvc = new MockMvcConfigurer()
                .restDocumentation(restDocumentation)
                .getMockMvc(controller);

        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    protected String json(Object object) {
        return objectMapper.writeValueAsString(object);
    }
}
