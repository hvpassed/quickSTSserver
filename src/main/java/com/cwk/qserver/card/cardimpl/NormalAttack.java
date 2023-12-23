package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-11-30 19:14
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 0)
public class NormalAttack extends AttackCard {

    protected static int getCardIdByAnn(){
        IsCard isCard= NormalAttack.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }



    public NormalAttack(){
        super(getCardIdByAnn());
        this.attackCount=1;
        this.damage = 100;
        this.title="普通攻击";
        this.description=String.format("造成%d点伤害",this.damage);
        this.cost=1;
        this.select= CardTargetConstant.SINGLE_MONSTER;
    }

    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;
        monster.damageApply(this.attackCount,this.damage);
    }
}
