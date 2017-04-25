package org.innovateuk.ifs.profile.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.junit.Assert.assertTrue;

public class ProfileRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProfileRepository> {

    @Override
    @Autowired
    protected void setRepository(ProfileRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Test
    public void saveWithInnovationArea() {
        loginPaulPlum();

        InnovationArea innovationArea = innovationAreaRepository.findByName("Emerging technology");
        Profile profile = newProfile().build();
        Profile savedProfile = repository.save(profile);
        savedProfile.addInnovationArea(innovationArea);
        savedProfile = repository.save(savedProfile);

        flushAndClearSession();

        Profile retrievedProfile = repository.findOne(savedProfile.getId());

        assertTrue(retrievedProfile.getInnovationAreas().contains(innovationArea));
    }
}
