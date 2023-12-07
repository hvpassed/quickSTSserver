package com.cwk.qserver.target;

import com.cwk.qserver.target.monsterimpl.BossMonster;

import java.util.HashSet;
import java.util.Set;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.target.monsterimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-07 18:01
 * @Description: TODO
 * @Version: 1.0
 */

public class BossSet {

    public static Set<Class> getBossSet(){
        Set<Class> set = new HashSet<>();
        set.add(BossMonster.class);
        return set;
    }

}
