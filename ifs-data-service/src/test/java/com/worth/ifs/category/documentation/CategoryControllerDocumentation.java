package com.worth.ifs.category.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.controller.AddressController;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.category.controller.CategoryController;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AddressDocs.addressResourceBuilder;
import static com.worth.ifs.documentation.CategoryDocs.categoryResourceBuilder;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class CategoryControllerDocumentation extends BaseControllerMockMVCTest<CategoryController> {
    private RestDocumentationResultHandler document;

    @Override
    protected CategoryController supplyControllerUnderTest() {
        return new CategoryController();
    }

    @Before
    public void setup(){
        this.document = document("category/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findByType() throws Exception {
        List<CategoryResource> categoryResources = categoryResourceBuilder.build(2);

        when(categoryServiceMock.getByType(CategoryType.INNOVATION_SECTOR)).thenReturn(serviceSuccess(categoryResources));

        mockMvc.perform(get("/category/findByType/{type}", CategoryType.INNOVATION_SECTOR.toString()))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("type").description("category type to filter on")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list with all categories that have the search type")
                        )
                ));
    }


    @Test
    public void findByParentId() throws Exception {
        List<CategoryResource> categoryResources = categoryResourceBuilder.build(2);

        when(categoryServiceMock.getByParent(anyLong())).thenReturn(serviceSuccess(categoryResources));

        mockMvc.perform(get("/category/findByParent/{parentId}", 1L))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("parentId").description("parent id to filter on")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list with all categories that have the parent id")
                        )
                ));
    }
}