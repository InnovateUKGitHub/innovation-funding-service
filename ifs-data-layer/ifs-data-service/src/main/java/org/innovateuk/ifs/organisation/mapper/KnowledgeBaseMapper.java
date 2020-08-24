package org.innovateuk.ifs.organisation.mapper;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.KNOWLEDGE_BASE;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                OrganisationTypeMapper.class,
                AddressMapper.class
        }
)
public abstract class KnowledgeBaseMapper extends BaseMapper<KnowledgeBase, KnowledgeBaseResource, Long> {

    @Autowired
    private AddressMapper addressMapper;

    @Mappings({
            @Mapping(source = "organisationType.name", target = "organisationTypeName")
    })
    @Override
    public abstract KnowledgeBaseResource mapToResource(KnowledgeBase domain);

    public Long mapKnowledgeBaseToId(KnowledgeBase object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}