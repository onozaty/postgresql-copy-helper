package com.github.onozaty.postgresql.copy;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * @author onozaty
 */
@Value
@Builder
public class Metadata {

    private final String tableName;

    @Singular
    private final List<String> columnNames;
}
