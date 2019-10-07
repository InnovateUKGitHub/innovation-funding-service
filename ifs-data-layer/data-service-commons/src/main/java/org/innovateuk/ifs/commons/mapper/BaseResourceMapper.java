package org.innovateuk.ifs.commons.mapper;


public abstract class BaseResourceMapper<D, R> {

    public abstract R mapToResource(D domain);
    public abstract Iterable<R> mapToResource(Iterable<D> domain);
}
