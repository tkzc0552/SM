package com.zhm.util;



import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by 赵红明 on 2019/6/4.
 */
@SuppressWarnings({"unused"})
public class EnumMapUtils {
    public static <E extends Enum<E>> int toBinary(Map<E, Boolean> enumMap) {
        if(enumMap == null || enumMap.isEmpty()) {
            return 0;
        }
        return enumMap.keySet().stream().filter(enumMap::get).mapToInt(enu -> 1 << enu.ordinal()).reduce(0, (a, b) -> a | b);
    }

    public static <E extends Enum<E>> Map<E, Boolean> toEnumMap(Class<E> clazz, int value) {
        Map<E, Boolean> enumMap = Maps.newHashMap();
        for (E enu : EnumSet.allOf(clazz)) {
            if ((value & (1 << enu.ordinal())) == (1 << enu.ordinal())) {
                enumMap.put(enu, true);
            } else {
                enumMap.put(enu, false);
            }
        }
        return enumMap;
    }

    public static <E extends Enum<E>> boolean is(E enu, int value) {
        return enu.ordinal() == (enu.ordinal() & value);
    }

    public static <E extends Enum<E>> boolean hasAll(EnumSet<E> enumSet, int value) {
        boolean bool;
        if (null != enumSet) {
            for (E enu : enumSet) {
                bool = enu.ordinal() == (enu.ordinal() & value);
                if (!bool) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <E extends Enum<E>> int toBinary(Collection<E> enums) {
        int value = 0;
        if (null != enums) {
            for (E enu : enums) {
                value |= 1 << enu.ordinal();
            }
        }
        return value;
    }

    public static <E extends Enum<E>> int allTrue(Class<E> clazz) {
        return EnumSet.allOf(clazz).stream().mapToInt(enu -> (1 << enu.ordinal())).reduce(0, (a, b) -> a | b);
    }

    public static <E extends Enum<E>> Map<E, Boolean> newEnumMap(E enu, boolean bool) {
        Map<E, Boolean> enumMap = Maps.newHashMap();
        enumMap.put(enu, bool);
        return enumMap;
    }
}
