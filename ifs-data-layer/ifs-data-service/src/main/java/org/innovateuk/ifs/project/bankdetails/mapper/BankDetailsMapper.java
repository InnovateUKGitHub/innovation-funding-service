package org.innovateuk.ifs.project.bankdetails.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                AddressMapper.class,
                OrganisationMapper.class

        },
        unmappedTargetPolicy = WARN
)
public abstract class BankDetailsMapper extends BaseMapper<BankDetails, BankDetailsResource, Long> {

        @Mappings({
                @Mapping(target = "address", source = "organisationAddress.address")
        })
        @Override
        public abstract BankDetailsResource mapToResource(BankDetails domain);

        public Long mapBankDetailsToId(BankDetails object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }
}
