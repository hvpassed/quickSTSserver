package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-05 23:23
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 2)
public class DoubleAttack extends AttackCard {


    protected static int getCardIdByAnn(){
        IsCard isCard= DoubleAttack.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }
    public DoubleAttack(){
        super(getCardIdByAnn());
        this.attackCount=2;
        this.damage = 15;
        this.title="双重攻击";
        this.description=String.format("造成%d次%d点伤害",this.attackCount,this.damage);
        this.cost=2;
        this.select= CardTargetConstant.SINGLE_MONSTER;
    }


    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;
        monster.damageApply(this.attackCount,this.damage);


    }
}
