package com.xpedia.backend.support;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

/** Valores distintos por campo para detectar pérdidas o cruces en los mappers. */
public final class ContractData {
    private ContractData() {}
    public static <T> T full(Class<T> type) {
        try {
            if (type.isRecord()) {
                var fields = type.getRecordComponents();
                var types = Arrays.stream(fields).map(RecordComponent::getType).toArray(Class<?>[]::new);
                var values = Arrays.stream(fields).map(f -> value(f.getType(), f.getName())).toArray();
                return type.getDeclaredConstructor(types).newInstance(values);
            }
            T instance = type.getDeclaredConstructor().newInstance();
            for (var field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                field.set(instance, value(field.getType(), field.getName()));
            }
            return instance;
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
    private static Object value(Class<?> type, String field) {
        int number = Math.abs(field.hashCode() % 40) + 1;
        if (type == UUID.class) return UUID.nameUUIDFromBytes(field.getBytes(StandardCharsets.UTF_8));
        if (type == String.class) return "valor-" + field + "-ñ";
        if (type == Short.class || type == short.class) return (short) number;
        if (type == Integer.class || type == int.class) return number;
        if (type == Long.class || type == long.class) return (long) number;
        if (type == Boolean.class || type == boolean.class) return true;
        if (type == BigDecimal.class) return new BigDecimal("10.5");
        if (type == OffsetDateTime.class) return OffsetDateTime.parse("2026-10-08T10:00:00-03:00").plusMinutes(number);
        if (type == String[].class) return new String[]{"comunicación", "cliente"};
        if (type == List.class) return field.equals("prerrequisitoIds") ? List.of(UUID.nameUUIDFromBytes("prerrequisito".getBytes(StandardCharsets.UTF_8))) : List.of("comunicación", "cliente");
        if (type == Map.class) return Map.of("texto", "Contenido ñ", "secciones", List.of("Inicio", "Cierre"));
        if (type.isEnum()) return type.getEnumConstants()[type.getEnumConstants().length - 1];
        throw new AssertionError("Tipo no contemplado: " + type);
    }
    public static void sameRecordFields(Object actual, Object source) {
        String[] fields = Arrays.stream(actual.getClass().getRecordComponents()).map(RecordComponent::getName).toArray(String[]::new);
        assertThat(actual).usingRecursiveComparison().comparingOnlyFields(fields).isEqualTo(source);
    }
}
