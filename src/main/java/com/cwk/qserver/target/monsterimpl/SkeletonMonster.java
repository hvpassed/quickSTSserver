package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.target.monster;
import lombok.Data;


public class SkeletonMonster extends monster {
    public String name = "skeleton";

    public String description = "it's a skeleton";
    public SkeletonMonster(int maxhp){
        super(maxhp);
    }

}
