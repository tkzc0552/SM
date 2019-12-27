package com.zhm.essential.id;

import com.zhm.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 扩展hibernate生成id的uuid生成器
 @author geewit
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class UUIDGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return UUID.randomUUID().toString();
    }
}
