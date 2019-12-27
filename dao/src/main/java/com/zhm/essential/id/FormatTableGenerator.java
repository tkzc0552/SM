package com.zhm.essential.id;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 扩展hibernate生成id的生成器
 @author geewit
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class FormatTableGenerator extends TableGenerator {

    public static final String FORMAT_PARAM = "format";

    protected String formatValue;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(new LongType(), params, serviceRegistry);
        formatValue = params.getProperty(FORMAT_PARAM);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        Serializable generated = super.generate(session, obj);
        if(generated instanceof Number) {
            if(formatValue == null) {
                return String.valueOf(generated);
            }
            return String.format(formatValue, generated);
        }
        return generated;
    }
}
