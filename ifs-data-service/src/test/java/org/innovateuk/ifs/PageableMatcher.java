package org.innovateuk.ifs;

import com.google.common.collect.Lists;
import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.List;

/**
 * Matcher for pageable objects in a repository call
 */
public class PageableMatcher extends ArgumentMatcher<Pageable> {
    public static Sort srt(String field, Direction dir) {
        Sort sort = new Sort();
        sort.setField(field);
        sort.setDirection(dir);
        return sort;
    }


    private int expectedPage;
    private int expectedPageSize;
    private Sort[] sortFields;

    public PageableMatcher(int expectedPage, int expectedPageSize, Sort... sortFields) {
        this.expectedPage = expectedPage;
        this.expectedPageSize = expectedPageSize;
        this.sortFields = sortFields;
    }

    @Override
    public boolean matches(Object argument) {
        Pageable arg = (Pageable) argument;

        if (!(expectedPage == arg.getPageNumber())) {
            return false;
        }

        if (!(expectedPageSize == arg.getPageSize())) {
            return false;
        }

        if (arg.getSort() == null) {
            return true;
        }

        List<Order> sortList = Lists.newArrayList(arg.getSort().iterator());

        if (sortList.size() != sortFields.length) {
            return false;
        }

        for (int i = 0; i < sortFields.length; i++) {
            Sort sortField = sortFields[i];
            Order order = sortList.get(i);
            if (!sortField.getDirection().equals(order.getDirection())) {
                return false;
            }
            if (!sortField.getField().equals(order.getProperty())) {
                return false;
            }
        }

        return true;
    }

    public static class Sort {
        private String field;
        private Direction direction;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }
}
