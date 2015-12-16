package com.worth.ifs;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.function.Predicate;

public class CustomMatchers {

    public static <T> PredicateMatcher<T> matchPred(Predicate<T> predicate){
        return new PredicateMatcher<T>(predicate);
    }



    private static class PredicateMatcher<T> extends BaseMatcher<T> {

        private Predicate<T> predicate;

        private PredicateMatcher(Predicate<T> predicate){
            this.predicate = predicate;
        }

        @Override
        public boolean matches(Object item) {
            return predicate.test((T) item);
        }


        @Override
        public void describeTo(Description description) {
        }
    }

}
