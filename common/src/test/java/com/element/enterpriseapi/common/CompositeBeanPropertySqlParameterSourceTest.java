package com.element.enterpriseapi.common;

import lombok.Value;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Types;
import java.util.Map;

class CompositeBeanPropertySqlParameterSourceTest implements WithAssertions {
    private final SqlParameterSource source = new CompositeBeanPropertySqlParameterSource(
            new FooAddress("Yonge", "Toronto"),
            Map.of(
                    "province", "ON",
                    "city", "New York"
            )
    );

    @Test
    void hasValue() {
        assertThat(source.hasValue("street")).isTrue();
        assertThat(source.hasValue("city")).isTrue();
        assertThat(source.hasValue("province")).isTrue();
        assertThat(source.hasValue("country")).isFalse();
    }

    @Test
    void getParameterNames() {
        assertThat(source.getParameterNames())
                .containsExactlyInAnyOrder("street", "city", "province", "class"); // Object.getClass() adds another property
    }

    @Test
    void getSqlType() {
        assertThat(source.getSqlType("street")).isEqualTo(Types.VARCHAR);
        assertThat(source.getSqlType("city")).isEqualTo(JdbcUtils.TYPE_UNKNOWN);
        assertThat(source.getSqlType("province")).isEqualTo(JdbcUtils.TYPE_UNKNOWN); // Map returns unknown for all props
    }

    @Test
    void getValue() {
        assertThat(source.getValue("street")).isEqualTo("Yonge");
        assertThat(source.getValue("city")).isEqualTo("New York");
        assertThat(source.getValue("province")).isEqualTo("ON");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> source.getValue("country"))
                .withMessageStartingWith("Invalid property 'country'");
    }

    @Value
    private static class FooAddress {
        String street;
        String city;
    }

}