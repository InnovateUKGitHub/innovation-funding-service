package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.OrganisationSize;
import org.innovateuk.ifs.finance.mapper.OrganisationSizeMapper;
import org.innovateuk.ifs.finance.repository.OrganisationSizeRepository;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.transactional.OrganisationSizeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.finance.builder.OrganisationSizeResourceBuilder.newOrganisationSizeResource;
import static org.innovateuk.ifs.finance.domain.builder.OrganisationSizeBuilder.newOrganisationSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrganisationSizeServiceImplTest extends BaseServiceUnitTest<OrganisationSizeServiceImpl> {

    @Mock
    private OrganisationSizeRepository organisationSizeRepository;

    @Mock
    private OrganisationSizeMapper organisationSizeMapper;

    @Before
    public void setUp() throws Exception {

    }
    @Override
    protected OrganisationSizeServiceImpl supplyServiceUnderTest() {
        return new OrganisationSizeServiceImpl();
    }

    @Test
    public void testGetOrganisationSizes() {
        List<OrganisationSize> organisationSizes = newOrganisationSize().build(1);
        List<OrganisationSizeResource> organisationSizeResources = newArrayList(newOrganisationSizeResource().build());
        when(organisationSizeRepository.findAll()).thenReturn(organisationSizes);
        when(organisationSizeMapper.mapToResource(organisationSizes)).thenReturn(organisationSizeResources);

        ServiceResult<List<OrganisationSizeResource>> actual = service.getOrganisationSizes();

        assertThat(actual.isSuccess(), equalTo(true));
        assertThat(actual.getSuccessObject(), equalTo(organisationSizeResources));
    }
}