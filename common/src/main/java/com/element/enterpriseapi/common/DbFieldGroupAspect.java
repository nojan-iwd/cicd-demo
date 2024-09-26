package com.element.enterpriseapi.common;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class DbFieldGroupAspect {

    @SneakyThrows
    public DbFieldGroupResult processObject(Object obj, String group) {
        List<String> setStatements = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Map<String, Object> fieldData = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbFieldGroup.class)) {
                DbFieldGroup annotation = field.getAnnotation(DbFieldGroup.class);
                if (annotation.name().equals(group)) {
                    field.setAccessible(true);
                    Object value = getFieldValue(obj, field.getName());
                    if (value != null) {
                        String columnName = (annotation.column().isBlank() ? field.getName() : annotation.column());
                        setStatements.add(columnName + "=:" + field.getName());
                        fieldData.put(field.getName(), value);
                        columns.add(columnName);
                    }
                }
            }
        }

        return DbFieldGroupResult.builder()
                .setStatement(String.join(", ", setStatements))
                .hasDataChanges(!fieldData.isEmpty())
                .values(fieldData)
                .columns(columns)
                .insertColumns(String.join(", ", columns))
                .insertValues(fieldData.keySet().stream().map(o -> ":" + o).collect(Collectors.joining(", ")))
                .build();
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            Method getterMethod = findGetterMethod(clazz, field);

            if (getterMethod != null) {
                return getterMethod.invoke(obj);
            } else {
                return field.get(obj);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Method findGetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            return clazz.getMethod("get" + capitalizedFieldName);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            try {
                return clazz.getMethod("is" + capitalizedFieldName);
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
