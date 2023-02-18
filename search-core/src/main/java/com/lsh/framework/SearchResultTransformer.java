package com.lsh.framework;

import org.hibernate.transform.BasicTransformerAdapter;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchResultTransformer<T> extends BasicTransformerAdapter {

    private final Class<T> resultClass;
    private final Map<String, Field> resultFields;

    public SearchResultTransformer(Class<T> resultClass) {
        super();
        this.resultClass = resultClass;
        resultFields = Arrays.stream(resultClass.getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    @Override
    public T transformTuple(Object[] tuple, String[] aliases) {
        try {
            T t = resultClass.newInstance();

            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                Object value = tuple[i];
                if (Objects.isNull(value)) {
                    continue;
                }
                setFieldValue(t, alias, value);
            }
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setFieldValue(Object o, String alias, Object value) throws IllegalAccessException {
        String fieldName = resultFields.containsKey(alias) ? alias : UnderlineToCamelUtils.underlineToCamel(alias, true);
        if (resultFields.containsKey(fieldName)) {
            Field field = resultFields.get(fieldName);
            if (field.getType().isAssignableFrom(value.getClass()) || value.getClass().isAssignableFrom(field.getType())) {
                field.set(o, value);
                return;
            }
            if (value instanceof BigInteger) {
                if (field.getType().isAssignableFrom(Long.class)) {
                    field.set(o, ((BigInteger) value).longValue());
                }
            }
            if (value instanceof Date) {
                if (field.getType().equals(Date.class)) {
                    field.set(o, value);
                    return;
                }
                List<Class<?>> localClasses = Arrays.asList(LocalDate.class, LocalDateTime.class, LocalTime.class);
                boolean anyMatch = localClasses.stream().anyMatch(l -> field.getType().isAssignableFrom(l));
                if (!anyMatch) {
                    return;
                }
                Date d = (Date) value;
                Object date;
                if (field.getType().isAssignableFrom(LocalDate.class)) {
                    date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                    date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                } else {
                    date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                }
                field.set(o, date);
            }
        }
    }

    @Override
    public List<T> transformList(List list) {
        return list;
    }
}
