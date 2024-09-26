package com.element.enterpriseapi;

import com.element.enterpriseapi.common.CustomMessageConverter;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Optional;

public class ArchConditions {
    public static final ArchCondition<JavaClass> HAVE_CORRESPONDING_MESSAGE_CONVERTER = new ArchCondition<>("Have corresponding MessageConverter") {
        @Override
        public void check(JavaClass inputClass, ConditionEvents events) {
            String inputClassName = inputClass.getSimpleName();
            String expectedConverterClassName = inputClassName + "MessageConverter";
            Optional<JavaClass> converterClass = inputClass.getPackage().getClassesInPackageTree()
                    .stream()
                    .filter(c -> c.getSimpleName().equals(expectedConverterClassName))
                    .findFirst();
            if (converterClass.isEmpty()) {
                String message = String.format("Class %s does not have a corresponding CustomMessageConverter %s",
                        inputClass.getSimpleName(), expectedConverterClassName);
                events.add(SimpleConditionEvent.violated(inputClass, message));
            } else if (!converterClass.get().isAssignableTo(CustomMessageConverter.class)) {
                String message = String.format("Class %s is not assignable to CustomMessageConverter with generic parameter %s",
                        converterClass.get().getSimpleName(), inputClass.getSimpleName());
                events.add(SimpleConditionEvent.violated(inputClass, message));
            }

        }
    };
}
