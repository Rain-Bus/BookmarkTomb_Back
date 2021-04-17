package com.cn.bookmarktomb.config;

import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.entity.CodeInfo;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import com.cn.bookmarktomb.model.entity.UserInfo;
import com.cn.bookmarktomb.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
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
		execDetectAdmin();
	}

	public void execDetectAdmin() {
		if ((boolean) ConfigCache.get(ConfigCache.INIT_FLAG)) {
			try {
				Query query = MongoUtil.getEqQueryByParam("URole", true);
				ConfigCache.set(ConfigCache.ADMIN_FLAG, !mongoTemplate.find(query, UserInfo.class).isEmpty());
				ConfigCache.set(ConfigCache.STARTED_FLAG, true);
				ConfigCache.set(ConfigCache.STARTED_OK, true);
			} catch (DataAccessResourceFailureException e) {
				ConfigCache.set(ConfigCache.DATABASE_REACH, false);
				ConfigCache.set(ConfigCache.STARTED_FLAG, true);
				return;
			} catch (UncategorizedMongoDbException e) {
				ConfigCache.set(ConfigCache.DATABASE_AUTH, false);
				ConfigCache.set(ConfigCache.STARTED_FLAG, true);
				return;
			}

			if (!(boolean) ConfigCache.get(ConfigCache.ADMIN_FLAG)) {
				mongoTemplate.indexOps(CodeInfo.class).ensureIndex(
						new Index().on("CRTm", Sort.Direction.ASC).expire(0).named("removeAt"));
				mongoTemplate.indexOps(OnlineInfo.class).ensureIndex(
						new Index().on("ORTm", Sort.Direction.ASC).expire(0).named("removeAt"));
			}
		}
	}
}
