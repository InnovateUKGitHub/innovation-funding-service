package org.innovateuk.ifs.pagination;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PaginationViewModelTest {

    @Test
    public void start() {
        //On 1st page of 20 pages, 10 items per page, 200 items in total.
        DummyPageResource pageResource = new DummyPageResource(200, 20, 0, 10);

        PaginationViewModel viewModel = new PaginationViewModel(pageResource);
        // expected view:
        // (1) 2 3 4 5 6 7 ... 20  Showing results 1 - 10 of 200 results
        assertEquals(1, viewModel.getStartPage());
        assertEquals(7, viewModel.getEndPage());
        assertEquals(1, viewModel.getCurrentPage());
        assertEquals(1, viewModel.getCurrentElementsFrom());
        assertEquals(10, viewModel.getCurrentElementsTo());
        assertEquals(200, viewModel.getTotalElements());
    }

    @Test
    public void middle() {
        //On 10th page of 20 pages, 10 items per page, 200 items in total.
        DummyPageResource pageResource = new DummyPageResource(200, 20, 9, 10);

        PaginationViewModel viewModel = new PaginationViewModel(pageResource);
        // expected view:
        // 1 ... 8 9 (10) 11 12 ... 20  Showing results 91 - 100 of 200 results
        assertEquals(8, viewModel.getStartPage());
        assertEquals(12, viewModel.getEndPage());
        assertEquals(10, viewModel.getCurrentPage());
        assertEquals(91, viewModel.getCurrentElementsFrom());
        assertEquals(100, viewModel.getCurrentElementsTo());
        assertEquals(200, viewModel.getTotalElements());
    }

    @Test
    public void end() {
        //On last page of 20 pages, 10 items per page, 200 items in total.
        DummyPageResource pageResource = new DummyPageResource(200, 20, 19, 10);

        PaginationViewModel viewModel = new PaginationViewModel(pageResource);
        // expected view:
        // 1 ... 14 15 16 17 18 19 (20)  Showing results 191 - 200 of 200 results
        assertEquals(14, viewModel.getStartPage());
        assertEquals(20, viewModel.getEndPage());
        assertEquals(20, viewModel.getCurrentPage());
        assertEquals(191, viewModel.getCurrentElementsFrom());
        assertEquals(200, viewModel.getCurrentElementsTo());
        assertEquals(200, viewModel.getTotalElements());
    }

    private class DummyPageResource extends PageResource<Dummy> {
        public DummyPageResource(long totalElements, int totalPages, int number, int size) {
            super(totalElements, totalPages, Collections.emptyList(), number, size);
        }
    }
    private class Dummy {}
}
