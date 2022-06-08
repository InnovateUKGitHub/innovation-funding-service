package org.innovateuk.ifs.mockbean;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.cfg.MapperConfiguration;
import org.innovateuk.ifs.cfg.MockBeanRepositoryConfiguration;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles(IfsProfileConstants.MOCK_BEAN_TEST)
@Import({MockBeanRepositoryConfiguration.class, MapperConfiguration.class})
public abstract class MockBeanTest {

    /**
     *
     * Use: For stubbing repository layer down.
     *
     * Mappers are created for auto-wiring in MapperConfiguration (add any more that you need in here)
     *
     * Repositories are created in MockBeanRepositoryConfiguration, import with @Autowired then
     * treat as a mock (add any more that you need in here)
     *
     * Compose mapper and stub repo configuration as a secondary test profile to avoid the default component scan.
     * Test user sub-classes will @MockBean and @Autowire in the test as they see fit, but typically only
     * external services and db layer will be mocked.
     *
     * @Import({MoreConfig.class})
     * @SpringBootTest(classes = {Service.class, Controller.class.. etc})
     * public class UploadGrantAgreementComponentTest extends MockBeanTest {
     *
     * @Autowire
     * FooRepository;
     *
     * @MockBean
     * BarClass;
     *
     */
}
