package org.innovateuk.ifs.organisation.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public abstract KnowledgeBaseResource mapToResource(KnowledgeBase domain);

    public Long mapKnowledgeBaseToId(KnowledgeBase object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}