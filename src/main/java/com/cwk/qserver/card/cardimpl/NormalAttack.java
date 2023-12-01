package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
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
        this.damage = 6;
        this.title="普通攻击";
        this.description="造成6点伤害";
        this.cost=1;
    }

}
