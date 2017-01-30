package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class PublicContentGroupDataBuilder extends BaseDataBuilder<Void, PublicContentGroupDataBuilder>{

    public PublicContentGroupDataBuilder withPublicContentGroup(String competitionName, String heading, String content, PublicContentSectionType type) {
        return with(data -> {
            if (competitionName != null) {
                Competition competition = retrieveCompetitionByName(competitionName);
                PublicContent publicContent = publicContentRepository.findByCompetitionId(competition.getId());
                ContentSection section = publicContent.getContentSections().stream().filter(filterSection -> type.equals(filterSection.getType())).findAny().get();

                ContentGroup contentGroup = new ContentGroup();
                contentGroup.setContent(content);
                contentGroup.setContentSection(section);
                contentGroup.setHeading(heading);
                contentGroup.setPriority(0);
                contentGroupRepository.save(contentGroup);
            }
        });
    }

    public static PublicContentGroupDataBuilder newCompetitionFunderData(ServiceLocator serviceLocator) {
        return new PublicContentGroupDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private PublicContentGroupDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                          ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected PublicContentGroupDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PublicContentGroupDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
