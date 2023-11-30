package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsMonster;

@IsMonster
public class ZombieMonster extends Monster {
    public static String name="zombie";
    public static int type = 1;
    public static String description="it's a zombie";
    public static int basehp = 30;
    public ZombieMonster(int delta,int addOrDe) {
        super(name,description,basehp,delta,addOrDe,type);
    }
}
