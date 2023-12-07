package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.dao.IService.impl.IntentServiceimpl;
import com.cwk.qserver.dao.IService.impl.MonsterServiceimpl;
import com.cwk.qserver.dao.Intent;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.target.BattlePlayer;
import com.cwk.qserver.target.monsterImpl;
import com.cwk.qserver.utils.ApplicationContextUtil;
import com.cwk.qserver.utils.IsMonster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.target.monsterimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-07 17:55
 * @Description: TODO
 * @Version: 1.0
 */
@IsMonster
public class BossMonster extends Monster implements monsterImpl {

    public static String name = "Boss";
    public static int type=3;
    public static String description = "it's a boss";
    public static int basehp = 100;
    private static int baseAttack = 20;

    private final int baseBlock = 10;
    private double attackWeight = 0.5;

    private double defendWeight = 0.1;



    private double skillWeight = 0.3;//暂时先不做

    public BossMonster(Monster monster) throws NoSuchMethodException {
        super(name,type, description);
        this.blockClear=monster.getBlockClear();
        this.difficulty = (monster.getDifficulty());

        this.seed = monster.getSeed();
        this.block = monster.getBlock();
        this.maxhp=monster.getMaxhp();
        this.nowhp = monster.getNowhp();
        this.damageReduction = monster.getDamageReduction();

        this.monsterid = monster.getMonsterid();
        this.intentFieldMap = new HashMap<>();
        this.intentFieldMap.put(0, BossMonster.class.getMethod("AttackIntentApply", Intent.class,Object.class));
        this.intentFieldMap.put(1,BossMonster.class.getMethod("DefendIntentApply",Intent.class,Object.class));
        this.intentFieldMap.put(2,BossMonster.class.getMethod("SkillIntentApply",Intent.class,Object.class));

    }
    public BossMonster(int delta,int addOrDe,String pos,int seed) throws NoSuchMethodException {
        super(name,description,basehp,delta,addOrDe,type,pos);
        this.seed = seed;
        this.setDamageReduction(0);
        this.intentFieldMap = new HashMap<>();
        this.intentFieldMap.put(0, BossMonster.class.getMethod("AttackIntentApply",Intent.class,Object.class));
        this.intentFieldMap.put(1,BossMonster.class.getMethod("DefendIntentApply",Intent.class,Object.class));
        this.intentFieldMap.put(2,BossMonster.class.getMethod("SkillIntentApply",Intent.class,Object.class));
        MonsterServiceimpl monsterService = ApplicationContextUtil.getBean(MonsterServiceimpl.class);
        monsterService.saveOrUpdate(this);
    }
    @Override
    public Intent generateIntent(){
        Random random = new Random(seed);
        seed = random.nextInt();

        double intentRandom = random.nextDouble();
        int addOrDe = (int) (( random.nextInt(2)-0.5)*2);
        int aveAttack = baseAttack+this.getDifficulty();
        int maxDeltaAttack = (int) (aveAttack*0.4);
        int aveBlock = baseBlock+this.getDifficulty();
        int maxDeltaBlock = (int) (aveBlock*0.4);
        Intent intent = new Intent();

        if(intentRandom<attackWeight){
            //攻击
            intent.setAttackAmount(1);
            intent.setAttack(aveAttack+addOrDe*random.nextInt(maxDeltaAttack));
            intent.setAim(intent.getAims().get(0));
            intent.setType(0);
        }else if(intentRandom<attackWeight+defendWeight){
            intent.setBlockGain(aveBlock+addOrDe*random.nextInt(maxDeltaBlock));
            intent.setAim(intent.getAims().get(1));
            intent.setType(1);
        }else{
            intent.setBlockGain(aveBlock+addOrDe*random.nextInt(maxDeltaBlock));
            intent.setAim(intent.getAims().get(2));
            intent.setType(2);
        }
        intent.setMonsterid(this.getMonsterid());
        return intent;
    }

    @Override
    public Object IntentApply (Object object) throws RuntimeException, InvocationTargetException, IllegalAccessException {
        IntentServiceimpl intentService = ApplicationContextUtil.getBean(IntentServiceimpl.class);
        if(intentService==null){
            throw new RuntimeException("Could not find IntentServiceimpl");
        }
        Intent intent = intentService.getById( this.monsterid);
        if(intent==null){
            throw new RuntimeException("Could not find intent by monsterid:"+this.monsterid);
        }

        Method method = this.intentFieldMap.get(intent.getType());
        Object obj =  method.invoke(this,intent,object);
        Random random = new Random(seed);
        seed= random.nextInt();
        return obj;
    }

    public Object AttackIntentApply(Intent intent, Object battlePlayerObj){
        BattlePlayer battlePlayer = (BattlePlayer) battlePlayerObj;
        battlePlayer.ApplyDamage(intent.getAttack(),intent.getAttackAmount());
        return battlePlayer;
    }
    @Override
    public Object DefendIntentApply(Intent intent, Object monsterObj){
        //SkeletonMonster monster = (SkeletonMonster) monsterObj;

        this.setBlock(this.getBlock()+intent.getBlockGain());
        return this;
    }
    @Override
    public Object SkillIntentApply(Intent intent, Object object){
        this.nowhp +=20;
        return this;
    }
}

