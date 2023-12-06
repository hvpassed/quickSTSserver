package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;

import java.util.List;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-05 23:27
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 3)
public class SweepAttack extends AttackCard {
    protected static int getCardIdByAnn(){
        IsCard isCard= SweepAttack.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }
    public SweepAttack(){
        super(getCardIdByAnn());
        this.attackCount=1;
        this.damage = 10;
        this.title="横扫攻击";
        this.description=String.format("对所有敌人造成%d点伤害",this.damage);
        this.cost=1;
        this.select= CardTargetConstant.ALL_MONSTERS;
    }

    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;
        for (int i = 0; i < this.attackCount; i++) {
            int nowBlock = monster.getBlock();
            int nowHp = monster.getNowhp();
            int attackdamge = this.damage-monster.getDamageReduction();
            if (nowBlock > 0) {
                int damage = attackdamge - nowBlock;
                nowBlock = (Math.max(nowBlock - attackdamge, 0));
                monster.setBlock(nowBlock);
                if (damage > 0) {
                    monster.setNowhp(nowHp - damage);
                }
            } else {
                monster.setNowhp(nowHp - attackdamge);
            }
            if(monster.getNowhp()<0){
                monster.setNowhp(0);
            }
        }
    }
    @Override
    public void impactAll(List<Monster> objects) {
        for (Monster monster : objects
             ) {
            this.impact(monster);
        }
    }
}
