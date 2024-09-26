package com.element.enterpriseapi.common;

import lombok.Builder;
import lombok.Data;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

class DbFieldGroupAspectTest implements WithAssertions {

    @Builder
    @Data
    static class TestClass {
        @DbFieldGroup(name = "groupA")
        String field1;
        @DbFieldGroup(name = "groupA")
        String field2;
        @DbFieldGroup(name = "groupA", column = "field3_ext")
        String field3;
        @DbFieldGroup(name = "groupB")

        Integer field4;
        @DbFieldGroup(name = "groupB")
        LocalDateTime field5;
        @DbFieldGroup(name = "groupC", column = "field6_ext")
        BigDecimal field6;

    }

    @Test
    void shouldProcessObject() {
        TestClass test = TestClass.builder()
                .field1("1")
                .field2("2")
                .field3("3")
                .field4(4)
                .field5(LocalDateTime.now())
                .field6(BigDecimal.ONE)
                .build();

        DbFieldGroupAspect aspect = new DbFieldGroupAspect();
        DbFieldGroupResult groupA = aspect.processObject(test, "groupA");
        DbFieldGroupResult groupB = aspect.processObject(test, "groupB");
        DbFieldGroupResult groupC = aspect.processObject(test, "groupC");
        assertThat(groupA.getColumns()).containsExactly("field1", "field2", "field3_ext");
        assertThat(groupA.getSetStatement()).isEqualTo("field1=:field1, field2=:field2, field3_ext=:field3");
        assertThat(groupA.getValues()).containsAllEntriesOf(Map.of("field1", test.getField1(), "field2", test.getField2(), "field3", test.getField3()));
        assertThat(groupA.getInsertColumns()).isEqualTo("field1, field2, field3_ext");
        assertThat(groupA.getInsertValues()).isEqualTo(":field1, :field3, :field2");

        assertThat(groupB.getColumns()).containsExactly("field4", "field5");

        assertThat(groupC.getColumns()).containsExactly("field6_ext");
    }
}