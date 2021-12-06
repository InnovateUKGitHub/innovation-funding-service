package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder;
import org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;

public class ProfileSkillsDocs {

    public static final ProfileSkillsResourceBuilder profileSkillsResourceBuilder = newProfileSkillsResource()
            .withUser(1L)
            .withInnovationAreas(
                    newInnovationAreaResource()
                            .withName("Data", "Cyber Security")
                            .withSector(3L)
                            .withSectorName("Emerging and enabling technologies")
                            .build(2))
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS);

    public static final ProfileSkillsEditResourceBuilder profileSkillsEditResourceBuilder = newProfileSkillsEditResource()
            .withUser(1L)
            .withSkillsAreas("skills")
            .withBusinessType(BusinessType.BUSINESS);
}
