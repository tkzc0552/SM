package com.zhm.essential.jpa.convert;




import com.zhm.util.EnumMapUtils;

import javax.persistence.AttributeConverter;
import java.util.Map;

public abstract class AbstractEnumMapConverter<E extends Enum> implements AttributeConverter<Map<E, Boolean>, Integer> {
    protected Class<E> clazz;

    @SuppressWarnings({"unchecked"})
    @Override
    public Integer convertToDatabaseColumn(Map<E, Boolean> attribute) {
        return EnumMapUtils.toBinary(attribute);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Map<E, Boolean> convertToEntityAttribute(Integer columnValue) {
        return EnumMapUtils.toEnumMap(clazz, columnValue);
    }
}