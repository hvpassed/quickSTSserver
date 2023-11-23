package com.cwk.qserver.target;

import com.cwk.qserver.card.Card;
import lombok.Data;

@Data
public class monster {

    public int monsterid;
    public String name;
    public String description;
    public int basehp;
    public int maxhp;
    public int nowhp;

    public int block=0;
    public monster(){

    }

    public monster(int maxhp){
        this.maxhp=maxhp;
        this.nowhp=maxhp;
    }
    public void ApplyDamage(Card card){

    }
}
