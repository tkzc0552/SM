package com.zhm.essential.jpa.search;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 构造 {@link Specification} 的工具类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class DynamicSpecifications {
    private final static Logger logger = LoggerFactory.getLogger(DynamicSpecifications.class);

    /**
     * 从 {@link SearchFilter} 构造 {@link Specification}
     * @param  filters {@link SearchFilter}
     * @return {@link Specification}
     */
    public static <T>Specification<T> bySearchFilter(final Collection<SearchFilter> filters) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(filters != null && !filters.isEmpty()) {
                logger.debug("filters != null");
                List<Predicate> predicates = Lists.newArrayList();
                for(SearchFilter filter : filters) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("filter.fieldName = " + filter.fieldName());
                        logger.debug("filter.values = " + StringUtils.join(filter.values(), ","));
                        logger.debug("filter.operator = " + filter.operator());
                        logger.debug("root.model = " + root.getModel().getName());
                    }
                    Path path;
                    if(StringUtils.contains(filter.fieldName(), '$')) {
                        String[] fields = StringUtils.split(filter.fieldName(), '$');
                        path = root.<String>get(fields[0]);
                        for (int i = 1; i < fields.length; i++) {
                            path = path.get(fields[i]);
                            if(path == null) {
                                break;
                            }
                        }
                    } else {
                        path = root.<String>get(filter.fieldName());
                    }
                    if(path == null) {
                        break;
                    }

                    switch (filter.operator()) {
                        case EQ: {
                            logger.debug("case EQ");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], false, null);
                                if(value != null) {
                                    predicates.add(criteriaBuilder.equal(path, value));
                                }
                            }
                            break;
                        }
                        case NE: {
                            logger.debug("case NE");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], false, null);
                                if(value != null) {
                                    predicates.add(criteriaBuilder.notEqual(path, value));
                                }
                            }
                            break;
                        }
                        case LIKE: {
                            logger.debug("case LIKE");
                            if(filter.values() != null && filter.values().length > 0) {
                                if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                    predicates.add(criteriaBuilder.like(path, "%" + filter.values()[0].toString() + "%"));
                                }
                            }
                            break;
                        }
                        case LLIKE: {
                            logger.debug("case LLIKE");
                            if(filter.values() != null && filter.values().length > 0) {
                                if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                    predicates.add(criteriaBuilder.like(path, "%" + filter.values()[0].toString()));
                                }
                            }
                            break;
                        }
                        case RLIKE: {
                            logger.debug("case RLIKE");
                            if(filter.values() != null && filter.values().length > 0) {
                                if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                    predicates.add(criteriaBuilder.like(path, filter.values()[0].toString() + "%"));
                                }
                            }
                            break;
                        }
                        case GT: {
                            logger.debug("case GT");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], true, null);
                                if(value instanceof Comparable) {
                                    if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                        predicates.add(criteriaBuilder.greaterThan(path, (Comparable) value));
                                    }
                                }
                            }
                            break;
                        }
                        case GTE: {
                            logger.debug("case GTE");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], true, null);
                                if(value instanceof Comparable) {
                                    if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Comparable) value));
                                    }
                                }
                            }
                            break;
                        }
                        case LT: {
                            logger.debug("case LT");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], true, null);
                                if(value instanceof Comparable) {
                                    if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                        predicates.add(criteriaBuilder.lessThan(path, (Comparable) value));
                                    }
                                }
                            }
                            break;
                        }
                        case LTE: {
                            logger.debug("case LTE");
                            if(filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0], true, null);
                                if(value instanceof Comparable) {
                                    if(StringUtils.isNotEmpty(filter.values()[0].toString())) {
                                        predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Comparable) value));
                                    }
                                }
                            }
                            break;
                        }
                        case BETWEEN: {
                            logger.debug("case BETWEEN");
                            if(filter.values().length == 2) {
                                Object value1 = parseValue(path, filter.values()[0], true, BetweenType.LOW);
                                Object value2 = parseValue(path, filter.values()[1], true, BetweenType.HIGH);
                                if(value1 != null && value2 != null) {
                                    if(value1 instanceof Comparable && value2 instanceof Comparable) {
                                        predicates.add(criteriaBuilder.between(path, ((Comparable)value1), ((Comparable)value2)));
                                    }
                                } else if(value1 != null) {
                                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, ((Comparable)value1)));
                                } else if(value2 != null) {
                                    predicates.add(criteriaBuilder.lessThanOrEqualTo(path, ((Comparable)value2)));
                                }
                            }
                            break;
                        }
                        case IN: {
                            logger.debug("case IN");
                            if(filter.values() != null && filter.values().length > 0) {
                                predicates.add(path.in((Object[]) StringUtils.split(filter.values()[0].toString(), ',')));
                            }
                            break;
                        }
                        case NOTIN: {
                            logger.debug("case NOTIN");
                            if(filter.values() != null && filter.values().length > 0) {
                                predicates.add(criteriaBuilder.not(path.in((Object[]) StringUtils.split(filter.values()[0].toString(), ','))));
                            }
                            break;
                        }
                        case ISNULL: {
                            logger.debug("case IS NULL");
                            predicates.add(criteriaBuilder.isNull(path));
                            break;
                        }
                        case ISNOTNULL : {
                            logger.debug("case IS NOT NULL");
                            predicates.add(criteriaBuilder.isNotNull(path));
                            break;
                        }
                        default: {
                           /* throw new CustomizedException("错误的参数");*/
                        }
                    }
                }

                if(!predicates.isEmpty()) {
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            } else {
                logger.debug("filters == null");
            }

            return criteriaBuilder.conjunction();
        };
    }

    /**
     * 合并多个 {@link Specification}
     * @param firstSpecification 第一个{@link Specification}
     * @param specifications 需要合并的{@link Specification}
     * @return {@link Specification}
     */
    public static <T>Specification<T> mergeSpecification(Specification<T> firstSpecification, Specification<T>... specifications) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newArrayList(firstSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
            if(specifications != null) {
                for(Specification<T> specification : specifications) {
                    predicates.add(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
                }
            }
            if(!predicates.isEmpty()) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return criteriaBuilder.conjunction();
        };
    }

    private static Object parseValue(Path path, Object objValue, boolean needComparable, BetweenType betweenType) {
        if(path == null || objValue == null) {
            return null;
        }
        String strValue = objValue.toString();
        Class pathClass = path.getJavaType();
        if(String.class.isAssignableFrom(pathClass)) {
            return objValue;
        } else if(pathClass.isEnum()) {
            logger.debug("path is enum");
            try {
                return Enum.valueOf(pathClass, strValue);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                return null;
            }
        } else if(Date.class.isAssignableFrom(pathClass)) {
            try {
                if(betweenType != null) {
                    switch (betweenType) {
                        case LOW: {
                            return fromDate(strValue);
                        }
                        case HIGH: {
                            return toDate(strValue);
                        }
                        default: {
                           /* throw new CustomizedException("错误的参数");*/
                        }
                    }
                } else {
                    return date(strValue);
                }
            } catch (ParseException e) {
                logger.warn(e.getMessage(), e);
                return null;
            }
        } else if(Number.class.isAssignableFrom(pathClass)) {
            return org.springframework.util.NumberUtils.parseNumber(strValue, pathClass);
        }
        if(needComparable && !Comparable.class.isAssignableFrom(pathClass)) {
            return null;
        }
        return objValue;
    }

    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

    private static Date date(String date) throws ParseException {
        return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(date, PATTERNS);
    }

    private static Date betweenDate(String date, BetweenType betweenType) throws ParseException {
        Date fromDate;
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}", date)) {
            return date(date);
        } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date)) {
            fromDate = date(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            switch (betweenType) {
                case LOW: {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    break;
                }
                case HIGH: {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    break;
                }
                default: {
                    /*throw new CustomizedException("错误的参数");*/
                }
            }

            return calendar.getTime();
        }

        return null;
    }

    /**
     * 转换 {@link String} 类型的date为 {@link Date}类型 时分秒为00:00:00
     *
     * @param date {@link Date} 类型
     */
    private static Date fromDate(String date) throws ParseException {
        return betweenDate(date, BetweenType.LOW);
    }

    /**
     * 转换 {@link String} 类型的date为 {@link Date}类型 时分秒为23:59:59
     *
     * @param date {@link Date} 类型
     */
    private static Date toDate(String date) throws ParseException {
        return betweenDate(date, BetweenType.HIGH);
    }
}

