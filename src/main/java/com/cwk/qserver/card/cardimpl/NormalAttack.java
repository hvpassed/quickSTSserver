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
        this.damage = 30;
        this.title="普通攻击";
        this.description=String.format("造成%d点伤害",this.damage);
        this.cost=1;
        this.select= CardTargetConstant.SINGLE_MONSTER;
    }

    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;

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
