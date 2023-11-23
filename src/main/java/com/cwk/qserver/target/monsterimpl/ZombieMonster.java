package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.target.monster;
import lombok.Data;


public class ZombieMonster extends monster {
    public String name="zombie";

    public String description="it's a zombie";

    public ZombieMonster(int maxhp) {
        super(maxhp);
    }
}
