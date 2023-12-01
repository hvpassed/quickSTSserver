package com.cwk.qserver.card.factory;

import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data
@Component
@ConfigurationProperties(prefix = "init-cardspile-config")
public class CardsPileFactory{
    public List<Card> cardList = new ArrayList<>();




    @Bean
    @Scope("prototype")
    public CardsPile getObject() throws Exception {
        System.out.println(this.cardList);
        return new CardsPile(this.cardList);
    }

}
