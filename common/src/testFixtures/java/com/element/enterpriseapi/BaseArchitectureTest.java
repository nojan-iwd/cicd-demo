package com.element.enterpriseapi;

import com.element.enterpriseapi.lambda.LambdaInput;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.element.enterpriseapi.ArchConditions.HAVE_CORRESPONDING_MESSAGE_CONVERTER;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public abstract class BaseArchitectureTest {

    @Test
    void everyInputClassMustDefineConverterBean() {
        ArchRule rule = classes()
                .that().areAssignableTo(LambdaInput.class)
                .should(HAVE_CORRESPONDING_MESSAGE_CONVERTER);
        rule.check(new ClassFileImporter().importPackagesOf(this.getClass()));
    }
}
