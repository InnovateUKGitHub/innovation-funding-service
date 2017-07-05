package org.innovateuk.ifs.project.bankdetails.mapper;

import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                OrganisationAddressMapper.class,
                OrganisationMapper.class

        },
        unmappedTargetPolicy = WARN
)
public abstract class BankDetailsMapper extends BaseMapper<BankDetails, BankDetailsResource, Long> {
        public Long mapBankDetailsToId(BankDetails object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }
}
