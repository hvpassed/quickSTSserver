package com.cwk.qserver.dao;

import com.cwk.qserver.dao.entity.Map;
import com.cwk.qserver.dao.entity.Player;
import lombok.Data;

@Data
public class PlayMapUnion {
    public Player player;
    public Map map;
}
