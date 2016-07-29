package com.worth.ifs.application.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

/**
 * This class is used to setup and submit the form input values. On submit the values are converted into an Form object.
 * http://stackoverflow.com/a/4511716
 */
public class Form extends BaseBindingResultTarget {
    private Map<String, String> formInput;

    public Form() {
        this.formInput = new HashMap<>();
    }

    public Map<String, String> getFormInput() {
        return formInput;
    }

    public void setFormInput(Map<String, String> values) {
        this.formInput = values;
    }

    public void addFormInput(String key, String value){
        this.formInput.put(key, value);
    }

    public void addFormInput(String key, LocalDate value){
        this.formInput.put(key + "_day", value != null ? value.getDayOfMonth() + "" : "");
        this.formInput.put(key + "_month", value != null ? value.getMonthValue() + "" : "");
        this.formInput.put(key + "_year", value != null ? value.getYear() + "" : "");
    }

    public String getFormInput(String key){
        return this.formInput.get(key);
    }

    public LocalDate getFormInputLocalDate(String key){

        String day = this.formInput.get(key + "_day");
        String month = this.formInput.get(key + "_month");
        String year = this.formInput.get(key + "_year");

        if (isNumber(day) && isNumber(month) && isNumber(year)) {
            return LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Form form = (Form) o;

        return new EqualsBuilder()
                .append(formInput, form.formInput)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formInput)
                .toHashCode();
    }
}
