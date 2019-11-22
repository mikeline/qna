package com.netcracker.services;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/*
* Generates new uuid if given uuid is null
* */
public class IfNullUUIDGenerator extends UUIDGenerator {
    private String entityName;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry registry) {
        entityName = params.getProperty(ENTITY_NAME);
        super.configure(type, params, registry);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Serializable id = session
                .getEntityPersister(entityName, object)
                .getIdentifier(object, session);

        if (id == null) {
            return super.generate(session, object);
        } else {
            return id;
        }
    }

}

