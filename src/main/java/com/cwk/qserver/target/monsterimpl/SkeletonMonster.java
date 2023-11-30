package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsMonster;

@IsMonster
public class SkeletonMonster extends Monster {
    public static String name = "skeleton";
    public static int type;
    public static String description = "it's a skeleton";
    public static int basehp = 20;
    public SkeletonMonster(int delta,int addOrDe){
        super(name,description,basehp,delta,addOrDe,type);
    }

}
