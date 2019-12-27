package com.zhm.essential.jpa.search;

/**
 * @author geewit
 */
public enum Operator {
    LIKE, LLIKE, RLIKE, EQ, GT, LT, GTE, LTE, NE,
    /**
     * //BETWEEN_fieldname_LOW, BETWEEN_fieldname_HIGH
     */
    BETWEEN,
    IN, NOTIN, ISNULL, ISNOTNULL
}
