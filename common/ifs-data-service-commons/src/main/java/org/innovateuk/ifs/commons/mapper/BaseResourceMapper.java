package org.innovateuk.ifs.commons.mapper;


import java.util.List;

public abstract class BaseResourceMapper<D, R> {

    public abstract R mapToResource(D domain);

    public abstract List<R> mapToResource(List<D> domain);

}