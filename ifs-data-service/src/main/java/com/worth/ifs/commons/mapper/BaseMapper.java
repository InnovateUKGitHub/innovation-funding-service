package com.worth.ifs.commons.mapper;


import org.springframework.data.repository.CrudRepository;

public abstract class BaseMapper<D, R>{
    protected CrudRepository<D, Long> repository;

//    public abstract void setRepository(CrudRepository repository);

    public abstract R mapToResource(D domain);
    public abstract Iterable<R> mapToResource(Iterable<D> domain);
    public abstract D mapToDomain(R resource);
    public abstract Iterable<D> mapToDomain(Iterable<R> resource);
}