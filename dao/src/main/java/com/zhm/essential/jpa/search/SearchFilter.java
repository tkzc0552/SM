package com.zhm.essential.jpa.search;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 构造｛@link org.springframework.data.jpa.domain.Specification｝的工具类
 @author gelif
 @since  2015-5-18
 */
public class SearchFilter {

    private SearchFilter(String fieldName, Operator operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    private SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.values = new Object[]{value};
    }

    private SearchFilter(String fieldName, Operator operator, Object[] values) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.values = values;
    }

    private String fieldName;
    private Operator operator;
    private Object[] values;

    public static SearchFilter build(String fieldName, Operator operator) {
        return new SearchFilter(fieldName, operator);
    }

    public static SearchFilter build(String fieldName, Operator operator, Object value) {
        return new SearchFilter(fieldName, operator, value);
    }


    public static SearchFilter build(String fieldName, Operator operator, Object[] values) {
        return new SearchFilter(fieldName, operator, values);
    }


    public static Collection<SearchFilter> parse(Map<String, Object> searchParams) {
        Set<SearchFilter> filters = Sets.newHashSet();
        Map<String, Object[]> betweens = null;
        SearchFilter filter;
        for(Entry<String, Object> entry : searchParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String[] names = StringUtils.split(key, "_");
            if(names.length != 2 && names.length != 3) {
                continue;
            }
            String fieldName = names[1];
            Operator operator = Operator.valueOf(names[0].toUpperCase());
            if(Operator.BETWEEN.equals(operator)) {
                if(names.length != 3) {
                    continue;
                }
                if(betweens == null) {
                    betweens = Maps.newHashMap();
                }
                BetweenType betweenType = BetweenType.valueOf(names[2].toUpperCase());
                Object[] fieldArray = betweens.get(fieldName);
                if(fieldArray == null) {
                    fieldArray = new Object[]{null, null};
                }
                switch (betweenType) {
                    case LOW: {
                        fieldArray[0] = value;
                        break;
                    }
                    case HIGH: {
                        fieldArray[1] = value;
                        break;
                    }
                    default:{
                        //throw new CustomizedException("错误的参数");
                    }
                }
                betweens.put(fieldName, fieldArray);
            } else {
                filter = new SearchFilter(fieldName, operator, value);
                filters.add(filter);
            }
        }
        if(betweens != null && !betweens.isEmpty()) {
            for(Entry<String, Object[]> entry : betweens.entrySet()) {
                filter = new SearchFilter(entry.getKey(), Operator.BETWEEN, entry.getValue());
                filters.add(filter);
            }
        }
        return filters;
    }

    /**
     * 增加filter
     * @param filters
     * @param filter
     */
    public static void addFilter(Collection<SearchFilter> filters, SearchFilter filter) {
        boolean notExist = true;
        for(SearchFilter existFilter : filters) {
            if(existFilter.fieldName.equals(filter.fieldName)) {
                notExist = false;
                break;
            }
        }
        if(notExist) {
            filters.add(filter);
        }
    }


    /**
     * 是否存在filter
     * @param filters
     * @param fieldName
     */
    public static boolean exists(Collection<SearchFilter> filters, String fieldName) {
        for(SearchFilter existFilter : filters) {
            if(existFilter.fieldName.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public String fieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Operator operator() {
        return operator;
    }

    public Object[] values() {
        return values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchFilter that = (SearchFilter) o;
        return Objects.equal(fieldName, that.fieldName) &&
                Objects.equal(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldName, operator);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fieldName", fieldName)
                .add("values", values)
                .add("operator", operator)
                .toString();
    }
}
