package io.kazuki.v0.internal.v1schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for index column specifications (for user-defined secondary indexes).
 */
public class IndexAttribute {
    public final String name;
    public final SortOrder sortOrder;
    public final AttributeTransform transform;

    @JsonCreator
    public IndexAttribute(@JsonProperty("name") String name,
            @JsonProperty("transform") AttributeTransform transform,
            @JsonProperty("sort") SortOrder sortOrder) {
        if (name == null) {
            throw new IllegalArgumentException("'name' must not be null");
        }

        if (sortOrder == null) {
            throw new IllegalArgumentException("'sort' must not be null");
        }

        this.name = name;
        this.sortOrder = sortOrder;
        this.transform = (transform == null) ? AttributeTransform.NONE
                : transform;
    }

    public String getName() {
        return name;
    }

    public AttributeTransform getTransform() {
        return transform;
    }

    @JsonProperty("sort")
    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
