package com.huang.template;

import com.huang.common.templates.DirectiveHandler;
import com.huang.common.templates.TemplateDirective;
import com.huang.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HotsTemplate extends TemplateDirective {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String weekRankKey = "week:rank";
        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(weekRankKey, 0, 6);
        List<Map> hotPosts = new ArrayList<>();
        for (ZSetOperations.TypedTuple typedTuple : typedTuples) {
            String key = "rank:post" + typedTuple.getValue();
            Map<String, Object> map = new HashMap();
            map.put("id", typedTuple.getValue());
            map.put("commentCount", redisUtil.hget(key, "post:commentCount"));
            map.put("title", redisUtil.hget(key, "post:title"));
            hotPosts.add(map);
        }
        handler.put(RESULTS, hotPosts).render();

    }
}
