package com.cn.bookmarktomb;

import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author Fallen-Angle
 */
@EnableOpenApi
@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories("com.cn.bookmarktomb.service")
public class BookmarkTombApplication {

    public static void main(String[] args) {
        ConfigCache.initCache();
        SpringApplication.run(BookmarkTombApplication.class, args);
    }
}