package com.cc.blogserver.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cc.blogserver.constant.RedisKey;
import com.cc.blogserver.domain.Article;
import com.cc.blogserver.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 文章浏览量回写任务
 * 前台详情浏览量在 Redis 计数，定时累加回写数据库并清理计数
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleViewCountTask {

    /** 回写间隔(毫秒)：5分钟 */
    private static final long FLUSH_INTERVAL = 5 * 60 * 1000L;

    private final StringRedisTemplate stringRedisTemplate;
    private final ArticleMapper articleMapper;

    @Scheduled(fixedDelay = FLUSH_INTERVAL)
    public void flushViewCounts() {
        String scanPattern = RedisKey.ARTICLE_VIEW_COUNT.getPattern().replace("%s", "*");
        Set<String> keys = stringRedisTemplate.keys(scanPattern);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        int flushedCount = 0;
        for (String key : keys) {
            String value = stringRedisTemplate.opsForValue().get(key);
            if (value == null) {
                stringRedisTemplate.delete(key);
                continue;
            }
            long count = Long.parseLong(value);
            if (count <= 0) {
                stringRedisTemplate.delete(key);
                continue;
            }
            long articleId = Long.parseLong(key.substring(key.lastIndexOf(':') + 1));
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, articleId)
                    .setSql("view_count = view_count + " + count));
            stringRedisTemplate.delete(key);
            flushedCount++;
        }
        log.info("文章浏览量回写完成, 回写文章数={}", flushedCount);
    }
}
