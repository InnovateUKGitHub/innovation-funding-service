package org.innovateuk.ifs.category.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.controller.CategoryController;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.documentation.InnovationAreaResourceDocs;
import org.innovateuk.ifs.documentation.InnovationSectorResourceDocs;
import org.innovateuk.ifs.documentation.ResearchCategoryResourceDocs;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CategoryDocs.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class CategoryControllerDocumentation extends BaseControllerMockMVCTest<CategoryController> {

    @Mock
    private CategoryService categoryServiceMock;

    @Override
    protected CategoryController supplyControllerUnderTest() {
        return new CategoryController();
    }

    @Test
    public void findInnovationAreas() throws Exception {
        List<InnovationAreaResource> innovationAreaResources = innovationAreaResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationAreas()).thenReturn(serviceSuccess(innovationAreaResources));

        mockMvc.perform(get("/category/find-innovation-areas")
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findInnovationAreasExcludingNone() throws Exception {
        List<InnovationAreaResource> innovationAreaResources = innovationAreaResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationAreas()).thenReturn(serviceSuccess(innovationAreaResources));

        mockMvc.perform(get("/category/find-innovation-areas-excluding-none")
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findInnovationSectors() throws Exception {
        List<InnovationSectorResource> innovationSectorResources = innovationSectorResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationSectors()).thenReturn(serviceSuccess(innovationSectorResources));

        mockMvc.perform(get("/category/find-innovation-sectors")
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findResearchCategories() throws Exception {
        List<ResearchCategoryResource> researchCategoryResources = researchCategoryResourceBuilder.build(2);

        when(categoryServiceMock.getResearchCategories()).thenReturn(serviceSuccess(researchCategoryResources));

        mockMvc.perform(get("/category/find-research-categories")
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findByInnovationSector() throws Exception {
        List<InnovationAreaResource> innovationAreaResources = innovationAreaResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationAreasBySector(anyLong())).thenReturn(serviceSuccess(innovationAreaResources));

        mockMvc.perform(get("/category/find-by-innovation-sector/{sectorId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}
