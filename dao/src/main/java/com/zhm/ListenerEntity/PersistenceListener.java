package com.zhm.ListenerEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;
import java.util.Date;

/**
 * JAVABEAN公用部分
 * Created by 赵红明 on 2019/6/17.
 */
public class PersistenceListener {
    @PrePersist
    public void prePersist(ListenerEntity listenedEntity) {
        Date now = Calendar.getInstance().getTime();
        if(listenedEntity.getCreateTime() == null) {
            listenedEntity.setCreateTime(now);
        }
        if(listenedEntity.getUpdateTime() == null) {
            listenedEntity.setUpdateTime(now);
        }
        if(listenedEntity.getDeleteFlag() == null) {
            listenedEntity.setDeleteFlag(0);
        }
    }

    @PreUpdate
    public void preUpdate(ListenerEntity listenedEntity) {
        listenedEntity.setUpdateTime(Calendar.getInstance().getTime());
        if(listenedEntity.getDeleteFlag() == null) {
            listenedEntity.setDeleteFlag(0);
        }
    }
}
