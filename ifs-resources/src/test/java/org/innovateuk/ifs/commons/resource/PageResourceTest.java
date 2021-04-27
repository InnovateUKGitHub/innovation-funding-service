package org.innovateuk.ifs.commons.resource;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.innovateuk.ifs.commons.resource.PageResource.fromListZeroBased;
import static org.junit.Assert.assertEquals;

public class PageResourceTest {

    @Test
    public void testPageResourceFirstPage(){

        PageResource<?> pageSizeOneNullAndResults = fromListZeroBased(null, 0, 1);
        assertEquals(emptyList(), pageSizeOneNullAndResults.getContent());
        assertEquals(0, pageSizeOneNullAndResults.getTotalPages());

        PageResource<?> pageSizeOneAndNoResults = fromListZeroBased(listToN(0), 0, 1);
        assertEquals(emptyList(), pageSizeOneAndNoResults.getContent());
        assertEquals(0, pageSizeOneAndNoResults.getTotalPages());

        PageResource<?> pageSizeOneAndOneResult = fromListZeroBased(listToN(1), 0, 1);
        assertEquals(asList(1), pageSizeOneAndOneResult.getContent());
        assertEquals(1, pageSizeOneAndOneResult.getTotalPages());

        PageResource<?> pageSizeFourAndEightResults = fromListZeroBased(listToN(8), 0, 4);
        assertEquals(asList(1,2,3,4), pageSizeFourAndEightResults.getContent());
        assertEquals(2, pageSizeFourAndEightResults.getTotalPages());

        PageResource<?> pageSizeFourAndFourResults = fromListZeroBased(listToN(4), 0, 4);
        assertEquals(asList(1,2,3,4), pageSizeFourAndFourResults.getContent());
        assertEquals(1, pageSizeFourAndFourResults.getTotalPages());
    }

    @Test
    public void testPageResourceLast(){
        PageResource<?> pageSizeThreeAndNineResults = fromListZeroBased(listToN(9), 2, 3);
        assertEquals(asList(7,8,9), pageSizeThreeAndNineResults.getContent());
        assertEquals(3, pageSizeThreeAndNineResults.getTotalPages());

        PageResource<?> pageSizeThreeAndSevenResults = fromListZeroBased(listToN(7), 2, 3);
        assertEquals(asList(7), pageSizeThreeAndSevenResults.getContent());
        assertEquals(3, pageSizeThreeAndSevenResults.getTotalPages());
    }

    @Test
    public void testPageResourceOffTheEnd() {
        PageResource<?> pageSizeThreeAndNineResults = fromListZeroBased(listToN(9), 3 /* past the last page */, 3);
        assertEquals(emptyList(), pageSizeThreeAndNineResults.getContent());
        assertEquals(3, pageSizeThreeAndNineResults.getTotalPages());
    }

    @Test
    public void testPageResourceBeforeTheBeginning() {
        PageResource<?> pageSizeThreeAndNineResults = fromListZeroBased(listToN(9), -1, 3);
        assertEquals(emptyList(), pageSizeThreeAndNineResults.getContent());
        assertEquals(3, pageSizeThreeAndNineResults.getTotalPages());
    }

    private List<Integer> listToN(int n){
        return rangeClosed(1, n).boxed().collect(toList());
    }

}
