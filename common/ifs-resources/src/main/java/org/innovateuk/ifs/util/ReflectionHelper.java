package org.innovateuk.ifs.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * For some reason someone decided to pull the whole of spring-boot-starter-test into main scope, so they could do
 * some dubious reflection in the builder classes.
 *
 * This is just a replacement for those calls as it's important to remove this transitive dependency in non-test scope,
 * it plays havoc with the AutoConfiguration.
 *
 * @deprecated Don't believe the ReflectionTestUtil was ever deemed ok to be used in main code in
 * the first place. Not sure if it would be easy to replace though.
 */
@Deprecated
public class ReflectionHelper {

    private static final String SETTER_PREFIX = "set";

    private static final boolean SPRING_AOP_PRESENT = ClassUtils.isPresent(
            "org.springframework.aop.framework.Advised", ReflectionHelper.class.getClassLoader());

    private ReflectionHelper() {}

    public static void setField(Object targetObject, String name, @Nullable Object value) {
        setField(targetObject, name, value, null);
    }

    /**
     * Set the {@linkplain Field field} with the given {@code name}/{@code type}
     * on the provided {@code targetObject} to the supplied {@code value}.
     * <p>This method delegates to {@link #setField(Object, Class, String, Object, Class)},
     * supplying {@code null} for the {@code targetClass} argument.
     * @param targetObject the target object on which to set the field; never {@code null}
     * @param name the name of the field to set; may be {@code null} if
     * {@code type} is specified
     * @param value the value to set
     * @param type the type of the field to set; may be {@code null} if
     * {@code name} is specified
     */
    public static void setField(Object targetObject, @Nullable String name, @Nullable Object value, @Nullable Class<?> type) {
        setField(targetObject, null, name, value, type);
    }

    /**
     * Set the static {@linkplain Field field} with the given {@code name} on
     * the provided {@code targetClass} to the supplied {@code value}.
     * <p>This method delegates to {@link #setField(Object, Class, String, Object, Class)},
     * supplying {@code null} for the {@code targetObject} and {@code type} arguments.
     * <p>This method does not support setting {@code static final} fields.
     * @param targetClass the target class on which to set the static field;
     * never {@code null}
     * @param name the name of the field to set; never {@code null}
     * @param value the value to set
     * @since 4.2
     */
    public static void setField(Class<?> targetClass, String name, @Nullable Object value) {
        setField(null, targetClass, name, value, null);
    }

    /**
     * Set the static {@linkplain Field field} with the given
     * {@code name}/{@code type} on the provided {@code targetClass} to
     * the supplied {@code value}.
     * <p>This method delegates to {@link #setField(Object, Class, String, Object, Class)},
     * supplying {@code null} for the {@code targetObject} argument.
     * <p>This method does not support setting {@code static final} fields.
     * @param targetClass the target class on which to set the static field;
     * never {@code null}
     * @param name the name of the field to set; may be {@code null} if
     * {@code type} is specified
     * @param value the value to set
     * @param type the type of the field to set; may be {@code null} if
     * {@code name} is specified
     * @since 4.2
     */
    public static void setField(
            Class<?> targetClass, @Nullable String name, @Nullable Object value, @Nullable Class<?> type) {

        setField(null, targetClass, name, value, type);
    }

    public static void setField(@Nullable Object targetObject, @Nullable Class<?> targetClass,
                                @Nullable String name, @Nullable Object value, @Nullable Class<?> type) {

        Assert.isTrue(targetObject != null || targetClass != null,
                "Either targetObject or targetClass for the field must be specified");

        if (targetObject != null && SPRING_AOP_PRESENT) {
            targetObject = getUltimateTargetObject(targetObject);
        }
        if (targetClass == null) {
            targetClass = targetObject.getClass();
        }

        Field field = ReflectionUtils.findField(targetClass, name, type);
        if (field == null) {
            throw new IllegalArgumentException(String.format(
                    "Could not find field '%s' of type [%s] on %s or target class [%s]", name, type,
                    safeToString(targetObject), targetClass));
        }

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, targetObject, value);
    }

    @Nullable
    public static Object getField(Object targetObject, String name) {
        return getField(targetObject, null, name);
    }

    @Nullable
    public static Object getField(Class<?> targetClass, String name) {
        return getField(null, targetClass, name);
    }

    @Nullable
    public static Object getField(@Nullable Object targetObject, @Nullable Class<?> targetClass, String name) {
        Assert.isTrue(targetObject != null || targetClass != null,
                "Either targetObject or targetClass for the field must be specified");

        if (targetObject != null && SPRING_AOP_PRESENT) {
            targetObject = getUltimateTargetObject(targetObject);
        }
        if (targetClass == null) {
            targetClass = targetObject.getClass();
        }

        Field field = ReflectionUtils.findField(targetClass, name);
        if (field == null) {
            throw new IllegalArgumentException(String.format("Could not find field '%s' on %s or target class [%s]",
                    name, safeToString(targetObject), targetClass));
        }

        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, targetObject);
    }

    private static String safeToString(@Nullable Object target) {
        try {
            return String.format("target object [%s]", target);
        }
        catch (Exception ex) {
            return String.format("target of type [%s] whose toString() method threw [%s]",
                    (target != null ? target.getClass().getName() : "unknown"), ex);
        }
    }

    private static <T> T getUltimateTargetObject(Object candidate) {
        Assert.notNull(candidate, "Candidate must not be null");
        try {
            if (AopUtils.isAopProxy(candidate) && candidate instanceof Advised) {
                Object target = ((Advised) candidate).getTargetSource().getTarget();
                if (target != null) {
                    return (T) getUltimateTargetObject(target);
                }
            }
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to unwrap proxied object", ex);
        }
        return (T) candidate;
    }

    public static void invokeSetterMethod(Object target, String name, Object value) {
        invokeSetterMethod(target, name, value, null);
    }

    public static void invokeSetterMethod(Object target, String name, @Nullable Object value, @Nullable Class<?> type) {
        Assert.notNull(target, "Target object must not be null");
        Assert.hasText(name, "Method name must not be empty");
        Class<?>[] paramTypes = (type != null ? new Class<?>[] {type} : null);

        String setterMethodName = name;
        if (!name.startsWith(SETTER_PREFIX)) {
            setterMethodName = SETTER_PREFIX + StringUtils.capitalize(name);
        }

        Method method = ReflectionUtils.findMethod(target.getClass(), setterMethodName, paramTypes);
        if (method == null && !setterMethodName.equals(name)) {
            setterMethodName = name;
            method = ReflectionUtils.findMethod(target.getClass(), setterMethodName, paramTypes);
        }
        if (method == null) {
            throw new IllegalArgumentException(String.format(
                    "Could not find setter method '%s' on %s with parameter type [%s]", setterMethodName,
                    safeToString(target), type));
        }

        ReflectionUtils.makeAccessible(method);
        ReflectionUtils.invokeMethod(method, target, value);
    }

}
