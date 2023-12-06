package com.cwk.qserver.card.factory;

import com.cwk.qserver.card.Card;
import com.cwk.qserver.utils.IsCard;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.factory
 * @Author: chen wenke
 * @CreateTime: 2023-11-30 20:04
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@Slf4j
@Component
public class CardFactory {
    private Map<Integer,Class<?>> cardIdMapCardClass = new HashMap<>();
    public static final int allCards = 5;
    public CardFactory(){
        Reflections reflections = new Reflections("com.cwk.qserver.card.cardimpl");
        Set<Class<?>> cardsClass = reflections.getTypesAnnotatedWith(IsCard.class);
        for (Class<?> clazz:
             cardsClass) {

            IsCard annotation =clazz.getAnnotation(IsCard.class);

            int cardid = annotation.cardId();
            cardIdMapCardClass.put(cardid,clazz);


        }

    }
    public List<Card> createCardById(Set<Integer> ids) throws RuntimeException{
        List<Card> ret = new ArrayList<>();
        for(Integer id:ids){
            Class<?> clazz;
            if(cardIdMapCardClass.containsKey(id)) {
                 clazz= cardIdMapCardClass.get(id);
            }else{
                log.error("Cannot find cardid:"+id);
                throw new RuntimeException();
            }
            Object obj;
            try {
                Constructor constructor= clazz.getDeclaredConstructor();
                obj = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                log.error("Cannot create Card cardid:"+id);
                throw new RuntimeException(e);
            }
            //((Card)obj).setCardid(id);
            ret.add((Card) obj);
        }

        return ret;
    }

}
