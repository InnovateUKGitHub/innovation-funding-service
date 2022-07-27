package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ApplicationMapper.class,
                OrganisationMapper.class,
                FileEntryMapper.class
        }
)
public abstract class ApplicationEoiEvidenceResponseMapper extends BaseMapper<ApplicationEoiEvidenceResponse, ApplicationEoiEvidenceResponseResource, Long>  {

    @Mappings({
            @Mapping(source = "application.id", target = "applicationId"),
            @Mapping(source = "organisation.id", target = "organisationId"),
            @Mapping(source = "fileEntry.id", target = "fileEntryId")
    })
    @Override
    public abstract ApplicationEoiEvidenceResponseResource mapToResource(ApplicationEoiEvidenceResponse domain);

    @Mappings({
            @Mapping(source = "applicationId", target = "application"),
            @Mapping(source = "organisationId", target = "organisation"),
            @Mapping(source = "fileEntryId", target = "fileEntry")
    })
    @Override
    public abstract ApplicationEoiEvidenceResponse mapToDomain(ApplicationEoiEvidenceResponseResource resource);

    public Long mapApplicationEoiEvidenceResponseToId(ApplicationEoiEvidenceResponse object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationEoiEvidenceResponse build() {
        return createDefault(ApplicationEoiEvidenceResponse.class);
    }
}
