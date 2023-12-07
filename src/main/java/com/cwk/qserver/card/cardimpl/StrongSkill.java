package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.SkillCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-06 15:52
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 6)
public class StrongSkill extends SkillCard {
    protected static int getCardIdByAnn(){
        IsCard isCard= StrongSkill.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }
    public StrongSkill(){
        super(getCardIdByAnn());
        this.title="强化";
        this.description="本场战斗，每回合开始时多获得1点能量";
        this.cost=2;
        this.select= CardTargetConstant.SELF;
    }

    @Override
    public void impact(Object obj) {
        BattlePlayer battlePlayer = (BattlePlayer) obj;
        battlePlayer.setCost(battlePlayer.getCost()+1);
    }
}
