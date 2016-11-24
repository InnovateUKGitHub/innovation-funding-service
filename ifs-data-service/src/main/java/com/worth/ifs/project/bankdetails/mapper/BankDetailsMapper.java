package com.worth.ifs.project.bankdetails.mapper;

import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.mapper.ProjectMapper;
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
