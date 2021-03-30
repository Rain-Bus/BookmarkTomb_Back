package com.cn.bookmarktomb.config;

import cn.hutool.core.util.StrUtil;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.entity.Database;
import com.mongodb.MongoCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Objects;

/**
 * @author fallen-angle
 * This is used to init the mongoTemplate;
 */
@Configuration
public class MongoConfig {

	@Bean
	public MongoClientFactoryBean mongo() {
		// Will give a default connection config while system hasn't been initialized.
		// Otherwise the spring will throw an error, will deal to can't boot.
		Database database = Objects.isNull(ConfigCache.get(ConfigCache.DATABASE))
				? getDefaultDatabase()
				: (Database) ConfigCache.get(ConfigCache.DATABASE);
		MongoClientFactoryBean mongo = new MongoClientFactoryBean();

		// Won't generate credential when mongoDB connection not need.
		if (StrUtil.isNotBlank(database.getUsername())) {
			MongoCredential credential =
					MongoCredential.createCredential(database.getUsername(), database.getDbname(), database.getPassword().toCharArray());
			mongo.setCredential(new MongoCredential[]{credential});
		}
		mongo.setHost(database.getHost());
		mongo.setPort(database.getPort());
		return mongo;
	}

	@Bean
	public MongoDatabaseFactory mongoDatabaseFactory() throws Exception {
		Database database = Objects.isNull(ConfigCache.get(ConfigCache.DATABASE))
				? getDefaultDatabase()
				: (Database) ConfigCache.get(ConfigCache.DATABASE);
		return new SimpleMongoClientDatabaseFactory(Objects.requireNonNull(mongo().getObject()), database.getDbname());
	}
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(Objects.requireNonNull(mongoDatabaseFactory()));
	}

	public Database getDefaultDatabase() {
		Database database = new Database();
		database.setHost("localhost");
		database.setDbname("bookmark_tomb");
		database.setPort(27017);
		return database;
	}
}
