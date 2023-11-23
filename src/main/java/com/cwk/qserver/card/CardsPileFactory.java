package com.cwk.qserver.card;

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
        return new CardsPile(this.cardList);
    }

}
