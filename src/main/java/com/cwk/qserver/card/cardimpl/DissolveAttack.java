package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-06 14:45
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 5)
public class DissolveAttack extends AttackCard {
    protected static int getCardIdByAnn(){
        IsCard isCard= DissolveAttack.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }
    public DissolveAttack(){
        super(getCardIdByAnn());
        this.attackCount=1;
        this.damage = 15;
        this.title="溶解攻击";
        this.description=String.format("溶解敌人护甲，然后造成%d伤害",this.damage);
        this.cost=1;
        this.select= CardTargetConstant.SINGLE_MONSTER;
    }

    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;
        monster.setBlock(0);
        monster.damageApply(this.attackCount,this.damage);
    }



}
