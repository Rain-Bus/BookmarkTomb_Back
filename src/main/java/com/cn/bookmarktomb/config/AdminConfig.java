package com.cn.bookmarktomb.config;

import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.entity.CodeInfo;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import com.cn.bookmarktomb.model.entity.UserInfo;
import com.cn.bookmarktomb.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author fallen-angle
 * Detect the admin has been created or not after boot.
 * If admin not created, will init the mongoDB's index;
 */
@Component
@RequiredArgsConstructor
public class AdminConfig implements CommandLineRunner {

	private final MongoTemplate mongoTemplate;

	@Override
	public void run(String... args) {
		if ((boolean) ConfigCache.get(ConfigCache.INIT_FLAG)) {
			Query query = MongoUtil.getEqQueryByParam("URole", true);
			ConfigCache.set(ConfigCache.ADMIN_FLAG, !mongoTemplate.find(query, UserInfo.class).isEmpty());
			if (!(boolean) ConfigCache.get(ConfigCache.ADMIN_FLAG)) {
				mongoTemplate.indexOps(CodeInfo.class).ensureIndex(
						new Index().on("CRTm", Sort.Direction.ASC).expire(0).named("removeAt"));
				mongoTemplate.indexOps(OnlineInfo.class).ensureIndex(
						new Index().on("ORTm", Sort.Direction.ASC).expire(0).named("removeAt"));
			}
		}
	}
}
