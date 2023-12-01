package com.cwk.qserver.target.monsterimpl;

import com.cwk.qserver.dao.Intent;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsMonster;

import java.util.Random;

@IsMonster
public class SkeletonMonster extends Monster {
    public static String name = "skeleton";
    public static int type;
    public static String description = "it's a skeleton";
    public static int basehp = 20;
    private final int baseAttack = 8;

    private final int baseBlock = 4;
    private double attackWeight = 0.8;

    private double defendWeight = 0.2;

    private double skillWeight = 0;//暂时先不做
    public SkeletonMonster(int delta,int addOrDe,String pos){
        super(name,description,basehp,delta,addOrDe,type,pos);
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
            intent.setAttack_amount(1);
            intent.setAttack(aveAttack+addOrDe*random.nextInt(maxDeltaAttack));
            intent.setAim(intent.getAims().get(0));
        }else if(intentRandom<attackWeight+defendWeight){
            intent.setBlockGain(aveBlock+addOrDe*random.nextInt(maxDeltaBlock));
            intent.setAim(intent.getAims().get(1));
        }else{

        }
        intent.setMonsterid(this.getMonsterid());
        return intent;
    }
}
