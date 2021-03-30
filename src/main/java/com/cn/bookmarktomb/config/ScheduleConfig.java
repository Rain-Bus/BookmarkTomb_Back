package com.cn.bookmarktomb.config;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.entity.Bookmark;
import com.cn.bookmarktomb.model.entity.Collection;
import com.cn.bookmarktomb.model.entity.Note;
import com.cn.bookmarktomb.model.entity.UserInfo;
import com.cn.bookmarktomb.util.JsonUtil;
import com.cn.bookmarktomb.util.MongoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fallen-angle
 * This is the schedules.
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduleConfig {

	private final MongoTemplate mongoTemplate;

	private List<Long> existUserIds;

	public ScheduleConfig(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

    /*---------------------------------------------------< Delete Schedule >------------------------------------------*/

	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteExpired() {
		LocalDateTime now = LocalDateTime.now();
		deleteUsers(now);
		getExistUserIds();
		deleteExpiredNote(now);
		deleteExpiredBookmark(now);
		deleteExpiredCollection(now);
	}

	private void deleteUsers(LocalDateTime removeTime) {
		Query query = MongoUtil.getAheadTimeQuery("URmTm", removeTime);
		long deleteCount = mongoTemplate.remove(query, UserInfo.class).getDeletedCount();
		log.info("Delete user number : " + deleteCount);
	}

	private void deleteExpiredNote(LocalDateTime removeTime) {
		Query expiredQuery = MongoUtil.getAheadTimeQuery("NRTm", removeTime);
		long deleteExpiredCount = mongoTemplate.remove(expiredQuery, Note.class).getDeletedCount();
		Query userNotExistQuery = new Query(Criteria.where("NOId").nin(existUserIds));
		long deleteUserNotExistCount = mongoTemplate.remove(userNotExistQuery, Note.class).getDeletedCount();
		log.info("Delete note number : " + (deleteExpiredCount + deleteUserNotExistCount));
	}

	private void deleteExpiredBookmark(LocalDateTime removeTime) {
		Query expiredQuery = MongoUtil.getAheadTimeQuery("BRTm", removeTime);
		long deleteExpiredCount = mongoTemplate.remove(expiredQuery, Bookmark.class).getDeletedCount();
		Query userNotExistQuery = new Query(Criteria.where("BOId").nin(existUserIds));
		long deleteUserNotExistCount = mongoTemplate.remove(userNotExistQuery, Bookmark.class).getDeletedCount();
		log.info("Delete bookmark number : " + (deleteExpiredCount + deleteUserNotExistCount));
	}

	private void deleteExpiredCollection(LocalDateTime removeTime) {
		Query expiredQuery = MongoUtil.getAheadTimeQuery("CRTm", removeTime);
		long deleteExpiredCount = mongoTemplate.remove(expiredQuery, Collection.class).getDeletedCount();
		Query userNotExistQuery = new Query(Criteria.where("COId").nin(existUserIds));
		long deleteUserNotExistCount = mongoTemplate.remove(userNotExistQuery, Collection.class).getDeletedCount();
		log.info("Delete collection number : " + (deleteExpiredCount + deleteUserNotExistCount));
	}

	private void getExistUserIds() {
		existUserIds = mongoTemplate.findAll(UserInfo.class)
				.stream().map(UserInfo::getId).collect(Collectors.toList());
	}


	/*---------------------------------------------------< Refresh Config >------------------------------------------*/

	/**
	 * Refresh config from conf file, detect user's directly change of conf;
	 */
	@Scheduled(cron = "*/5 * * * * ?")
	public void refreshConfigCache() {
		if ((boolean) ConfigCache.get(ConfigCache.INIT_FLAG)) {
			try {
				JSONObject jsonObject = JsonUtil.readJsonFile(ConfigCache.get(ConfigCache.CONF_PATH).toString());
				if (!MD5.create().digestHex(jsonObject.toString()).equals(ConfigCache.get(ConfigCache.MD_5))) {
					ConfigCache.initCache();
				}
			} catch (SystemException e) {
				ConfigCache.set(ConfigCache.INIT_FLAG, false);
				log.error("Error: Can't init config cache. " + e.getMessage());
			}
		}
	}
}
