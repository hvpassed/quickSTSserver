package com.cwk.qserver.target;

import com.cwk.qserver.dao.Intent;

import java.lang.reflect.InvocationTargetException;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.target
 * @Author: chen wenke
 * @CreateTime: 2023-12-04 12:58
 * @Description: TODO
 * @Version: 1.0
 */
public interface monsterImpl {
    public Object AttackIntentApply(Intent intent, Object obj);
    public Object DefendIntentApply(Intent intent, Object obj);
    public Object SkillIntentApply(Intent intent, Object obj);
    public Intent generateIntent();
    public Object IntentApply (Object object) throws RuntimeException, InvocationTargetException, IllegalAccessException;
}
