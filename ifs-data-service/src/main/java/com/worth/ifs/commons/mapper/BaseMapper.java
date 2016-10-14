package com.worth.ifs.commons.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseMapper<D, R, I extends Serializable> {
    protected CrudRepository<D, I> repository;
    @Autowired
    public void setRepository(CrudRepository<D, I> repository) {
        this.repository = repository;
    }


    public D mapIdToDomain(I id) {
        if(id == null){
            return null;
        }
        return repository.findOne(id);
    }

    public R mapIdToResource(I id) {
        D domain = mapIdToDomain(id);
        return domain != null ? mapToResource(domain) : null;
    }

    public abstract R mapToResource(D domain);
    public abstract Iterable<R> mapToResource(Iterable<D> domain);
    public abstract D mapToDomain(R resource);
    public abstract Iterable<D> mapToDomain(Iterable<R> resource);

    public static <T> T createDefault(Class<T> clazz) {
        // Copy and paste from BaseBuilderAmendFunctions
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Attempt to invoke non-existent default constructor on " + clazz.getName());
        }
    }
}
