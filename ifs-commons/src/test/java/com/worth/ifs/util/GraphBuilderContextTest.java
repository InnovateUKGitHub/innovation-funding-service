package com.worth.ifs.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class GraphBuilderContextTest {

    private static class A{
        B b;
        C c;
    }

    private static class B {
        A a;
        C c;
    }

    private static class C {
        A a;
        B b;
    }

    @Test
    public void testGraphBuilderContext(){
        // Build graph to copy
        A existingA = new A();
        B existingB = new B();
        C existingC = new C();
        existingA.b = existingB;
        existingA.c = existingC;
        existingB.a = existingA;
        existingB.c = existingC;
        existingC.a = existingA;
        existingC.b = existingB;
        A newA = mapA(existingA, new GraphBuilderContext());

        assertNotNull(newA);
        assertNotNull(newA.b);
        assertNotNull(newA.b.c);

        assertNotEquals(existingA, newA);
        assertNotEquals(existingA.b, newA.b);
        assertNotEquals(existingA.c, newA.c);

        assertEquals(newA, newA.b.c.a);
    }

    private A mapA(A a, GraphBuilderContext ctx){
        return ctx.resource(a, A::new, ar -> {
            ar.b = mapB(a.b, ctx);
            ar.c = mapC(a.c, ctx);
        });
    }

    private B mapB(B b, GraphBuilderContext ctx){
        return ctx.resource(b, B::new, br -> {
            br.a = mapA(b.a, ctx);
            br.c = mapC(b.c, ctx);
        });
    }

    private C mapC(C c, GraphBuilderContext ctx){
        return ctx.resource(c, C::new, cr -> {
            cr.a = mapA(c.a, ctx);
            cr.b = mapB(c.b, ctx);
        });
    }



}

