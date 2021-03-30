package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.EntityExistException;
import com.cn.bookmarktomb.excepotion.EntityNotFoundException;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.BookmarkConvert;
import com.cn.bookmarktomb.model.entity.Collection;
import com.cn.bookmarktomb.model.entity.Bookmark;
import com.cn.bookmarktomb.model.dto.BookmarkDTO.*;
import com.cn.bookmarktomb.model.vo.BookmarkVO.*;
import com.cn.bookmarktomb.model.vo.EntityExistVO;
import com.cn.bookmarktomb.util.MongoUtil;
import com.cn.bookmarktomb.service.NoteService;
import com.cn.bookmarktomb.service.BookmarkService;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fallen-angle
 */
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

	private final MongoTemplate mongoTemplate;
	private final BookmarkConvert bookmarkConvert;
	private final NoteService noteService;

	static final Map<String, Object> NOT_FOUND_MAX_ID_MAP =
			MapBuilder.<String, Object>create().put("maxBookmarkId", 0L).map();

	/*-------------------------------------------< Service >----------------------------------------------*/

	@Override
	public void insertBookmark(InsertBookmarkDTO insertBookmarkDTO) {
		if (!detectCollectionNotExist(insertBookmarkDTO.getOwnerId(),
				Collections.singletonList(insertBookmarkDTO.getParentId()))
				.isEmpty()) {
			insertBookmarkDTO.setParentId(null);
		}
		detectConflictBookmark(insertBookmarkDTO.getOwnerId(),
				Collections.singletonList(bookmarkConvert.insert2GetBookmarkDTO(insertBookmarkDTO)));
		mongoTemplate.insert(bookmarkConvert.insertBookmarkDTO2DO(insertBookmarkDTO));
	}

	@Override
	public void insertBookmarks(List<InsertBookmarkDTO> insertBookmarkDTOs) {
		Long userId = insertBookmarkDTOs.get(0).getOwnerId();
		List<GetBookmarkDTO> getDTOs = bookmarkConvert.insert2GetBookmarkDTOs(insertBookmarkDTOs);
		List<Long> notExistList = detectCollectionNotExist(userId,
				getDTOs.stream().map(GetBookmarkDTO::getParentId).collect(Collectors.toList()));
		insertBookmarkDTOs.forEach(bookmark -> bookmark.setParentId(
				notExistList.contains(bookmark.getParentId()) ? null : bookmark.getParentId()
		));
		detectConflictBookmark(userId, getDTOs);
		mongoTemplate.insertAll(bookmarkConvert.insertBookmarkDTO2DOs(insertBookmarkDTOs));
	}

	@Override
	public void deleteById(Long userId, List<Long> ids) {
		// Set the field of delete bookmark.
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", ids)
				.put("BRTm", null)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getDeleteUpdate(null);
		long deleteCount = mongoTemplate.updateMulti(query, update, Bookmark.class).getMatchedCount();

		// Get the remove time to delete the cascading deleted notes; The concrete implementation refer to UserBookCollectionServiceImpl.deleteCollectionsByIds().
		LocalDateTime removeTime = update.getUpdateObject().get("$set", Document.class).get("BRTm", LocalDateTime.class);
		noteService.deleteByBookmark(userId, ids, removeTime);

		if (deleteCount < ids.size()) {
			throw new EntityNotFoundException("Some bookmark not found when delete " + ids);
		}
	}

	@Override
	public void deleteByCollection(Long userId, List<Long> collectionIds, LocalDateTime removeTime) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BRTm", null)
				.put("BOId", userId)
				.put("BCId", collectionIds)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getDeleteUpdate(removeTime);

		// Get the bookmarks id of the collection; This will be used to remove note of the bookmark.
		// This must before update DB, if the DB updated, will can't get the bookmarks.
		List<Long> bookmarkIds = mongoTemplate.find(query, Bookmark.class)
				.stream().map(Bookmark::getId).collect(Collectors.toList());
		noteService.deleteByBookmark(userId, bookmarkIds, removeTime);
		mongoTemplate.updateMulti(query, update, Bookmark.class);
	}

	@Override
	public void restoreById(Long userId, List<Long> ids) {
		// Get the remove times, which will be used to restore the note which has been cascading deleted.
		List<LocalDateTime> removeTimes = getDeletedBookmarksByIds(userId, ids)
				.stream().map(GetBookmarkDTO::getRemoveTime).collect(Collectors.toList());
		noteService.restoreByBookmark(userId, ids, removeTimes);

		// Get the restore collection's parent collection, if the parent collection is not exist, will set the parent collection as default collection(my favorite),
		// and at last restore the bookmark(unset the collection's "BRTm")
		List<GetBookmarkDTO> bookmarkDTOs = getDeletedBookmarksByIds(userId, ids);
		detectConflictBookmark(userId, bookmarkDTOs);
		List<Long> collectionIds = bookmarkDTOs.stream()
				.map(GetBookmarkDTO::getParentId)
				.distinct()
				.collect(Collectors.toList());
		List<Long> notExistCollectionIds = detectCollectionNotExist(userId, collectionIds);
		updateToDefaultCollection(userId, ids, notExistCollectionIds);
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", ids)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "BRTm");
		Update update = getRestoreUpdate();
		if (mongoTemplate.updateMulti(query, update, Bookmark.class).getMatchedCount() < ids.size()) {
			throw new EntityNotFoundException("Some bookmark not found when restore " + ids);
		}
	}

	@Override
	public void restoreByCollection(Long userId, List<Long> collectionIds, LocalDateTime removeTime) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BOId", userId)
				.put("BCId", collectionIds)
				.put("BRTm", removeTime)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getRestoreUpdate();
		// Get the bookmark id of the collection, which will be used to restore note; Then, update the bookmarks unset "BRTm" by parent collection.
		List<Long> bookmarkIds = mongoTemplate.find(query, Bookmark.class)
				.stream().map(Bookmark::getId).collect(Collectors.toList());
		noteService.restoreByBookmark(userId, bookmarkIds, Collections.singletonList(removeTime));
		mongoTemplate.updateMulti(query, update, Bookmark.class);
	}

	@Override
	public void updateCommonInfo(ChangeBookmarkDTO changeBookmarkDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BRTm", null)
				.put("BId", changeBookmarkDTO.getId())
				.put("BOId", changeBookmarkDTO.getOwnerId())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getChangeCommonInfoUpdate(changeBookmarkDTO);
		if (mongoTemplate.updateFirst(query, update, Bookmark.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("Some bookmark not found when update " + changeBookmarkDTO.getId());
		}
	}

	@Override
	public void updateParent(ChangeParentCollectionDTO changeParentCollectionDTO) {
		Long fromParentId = changeParentCollectionDTO.getFromParentId();
		Long toParentId = changeParentCollectionDTO.getToParentId();
		if (Objects.equals(fromParentId, toParentId)) {
			throw new EntityExistException("Source and target is same!");
		}
		// Get the from and to collection query and update.
		List<Long> fromAndToCollectionIds = Arrays.asList(fromParentId, toParentId);
		fromAndToCollectionIds = fromAndToCollectionIds.stream().filter(Objects::nonNull).collect(Collectors.toList());
		Map<String, Object> fromAndToQueryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("CId", fromAndToCollectionIds)
				.put("COId", changeParentCollectionDTO.getOwnerId())
				.map();
		Query fromAndToQuery = MongoUtil.getEqQueryByMap(fromAndToQueryMap);
		Update fromAndToUpdate = getChangeParentFromAndToUpdate(changeParentCollectionDTO);
		if (mongoTemplate.updateMulti(fromAndToQuery, fromAndToUpdate, com.cn.bookmarktomb.model.entity.Collection.class).getModifiedCount() < fromAndToCollectionIds.size()) {
			throw new EntityNotFoundException("Some collection not found when update parents " + fromAndToCollectionIds);
		}

		Map<String, Object> bookmarkQueryMap = MapBuilder.<String, Object>create()
				.put("BRTm", null)
				.put("BCId", fromParentId)
				.put("BId", changeParentCollectionDTO.getIds())
				.put("BOId", changeParentCollectionDTO.getOwnerId())
				.map();
		Query bookmarkQuery = MongoUtil.getEqQueryByMap(bookmarkQueryMap);
		Update bookmarkUpdate = getChangeParentBookmarkUpdate(changeParentCollectionDTO);
		if (mongoTemplate.updateMulti(bookmarkQuery, bookmarkUpdate, Bookmark.class).getModifiedCount() < changeParentCollectionDTO.getIds().size()) {
			throw new EntityNotFoundException("Some bookmark not found when update parents " + changeParentCollectionDTO.getIds());
		}

	}

	@Override
	public void updateTop(Long userId, List<Long> ids) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", ids)
				.put("BRTm", null)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getTopUpdate();
		if (mongoTemplate.updateMulti(query, update, Bookmark.class).getMatchedCount() < ids.size()) {
			throw new EntityNotFoundException("Some bookmark not found when update top " + ids);
		}
	}

	@Override
	public void updateCancelTop(Long userId, List<Long> ids) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", ids)
				.put("BRTm", null)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getCancelTopUpdate();
		if (mongoTemplate.updateMulti(query, update, Bookmark.class).getMatchedCount() < ids.size()) {
			throw new EntityNotFoundException("Some bookmark not found when update cancel top " + ids.size());
		}
	}

	@Override
	public List<GetBookmarkDTO> listById(Long userId, List<Long> ids) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", ids)
				.put("BRTm", null)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		query.with(Sort.by(Sort.Direction.DESC, "BTTm", "BCTm"));
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public List<GetBookmarkDTO> listByOwner(Long userId, SortAndPageDTO sortAndPageDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BOId", userId)
				.put("BRTm", null)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap)
				.limit(sortAndPageDTO.getCount())
				.skip(sortAndPageDTO.getOffset())
				.with(Sort.by(sortAndPageDTO.getOrder(), sortAndPageDTO.getAccording()));
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public List<GetBookmarkDTO> listByCollection(Long userId, Long collectionId, SortAndPageDTO sortAndPageDTO) {
		return listByCollections(userId, Collections.singletonList(collectionId), sortAndPageDTO);
	}

	@Override
	public List<GetBookmarkDTO> listByCollections(Long userId, List<Long> collectionIds, SortAndPageDTO sortAndPageDTO) {
		MapBuilder<String, Object> queryMapBuilder = MapBuilder.create();
		queryMapBuilder.put("BOId", userId)
				.put("BCId", collectionIds);
		Query query;

		// If sortAndPageDTO is null, this present the query is used by selectLastModifyTime.
		// If will use it later, and only get the not deleted bookmark, maybe need add a argument as flag.
		if (Objects.nonNull(sortAndPageDTO)) {
			queryMapBuilder.put("BRTm", null);
			query = MongoUtil.getEqQueryByMap(queryMapBuilder.map());
			query.skip(sortAndPageDTO.getOffset())
					.limit(sortAndPageDTO.getCount())
					.with(Sort.by(sortAndPageDTO.getOrder(), sortAndPageDTO.getAccording()));
		} else {
			query = MongoUtil.getEqQueryByMap(queryMapBuilder.map());
		}

		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public List<GetBookmarkDTO> listByTag(Long userId, String tagName) {
		List<String> tagNameList = new ArrayList<>(1);
		tagNameList.add(tagName);
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BRTm", null)
				.put("BOId", userId)
				.put("BTag", tagNameList)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap)
				.with(Sort.by(Sort.Direction.ASC, "BMTm"));
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public List<GetBookmarkDTO> listDeletedByOwner(Long userId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "BRTm")
				.with(Sort.by(Sort.Direction.DESC, "BRTm"));
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public List<GetBookmarkDTO> listFavorByOwner(Long userId, SortAndPageDTO sortAndPageDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BCId", null)
				.put("BRTm", null)
				.put("BOId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap)
				.limit(sortAndPageDTO.getCount())
				.skip(sortAndPageDTO.getOffset())
				.with(Sort.by(sortAndPageDTO.getOrder(), sortAndPageDTO.getAccording()));
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	@Override
	public Map<Integer, GetBookmarkDTO> listCancelTopInfoAndIndex(Long userId, List<Long> cancelIds) {
		// Get the bookmarks' id of the parent collection sorted by db, then get the index of the bookmarks after cancel top,
		// and then return the index and bookmark info to simplify the operation of front-end sort.
		GetBookmarkDTO firstBookmark = Optional.ofNullable(selectById(userId, cancelIds.get(0)))
				.orElseThrow(() -> new EntityNotFoundException("Can't find id " + cancelIds.get(0)));
		Long collectionId = firstBookmark.getParentId();
		SortAndPageVO sortAndPageVO = new SortAndPageVO();
		sortAndPageVO.setAccording("defaults");
		sortAndPageVO.setOrder("desc");
		SortAndPageDTO sortAndPageDTO = bookmarkConvert.sortAndPageVO2DTO(sortAndPageVO);
		List<GetBookmarkDTO> dbSortedBookmarkDTOs = listByCollection(userId, collectionId, sortAndPageDTO);
		List<Long> dbSortedBookmarkIds = dbSortedBookmarkDTOs
				.stream().map(GetBookmarkDTO::getId).collect(Collectors.toList());
		return cancelIds.stream().collect(Collectors.toMap(
					dbSortedBookmarkIds::indexOf,
					k -> dbSortedBookmarkDTOs.get(dbSortedBookmarkIds.indexOf(k))
				));
	}

	@Override
	public GetBookmarkDTO selectById(Long userId, Long bookmarkId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BRTm", null)
				.put("BOId", userId)
				.put("BId", bookmarkId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		return bookmarkConvert.getBookmarkDO2DTO(mongoTemplate.findOne(query, Bookmark.class));
	}

	@Override
	public Long selectUserMaxId(Long userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("BOId").is(userId)),
				Aggregation.group("BOId").max("BId").as("maxBookmarkId")
		);
		Map<String, Object> resultMap =
				mongoTemplate.aggregate(aggregation, "bookmark_info", BasicDBObject.class)
						.getUniqueMappedResult();
		resultMap = Optional.ofNullable(resultMap)
				.orElse(NOT_FOUND_MAX_ID_MAP);
		return (Long)Optional.ofNullable(resultMap.get("maxBookmarkId"))
				.orElse(0L);
	}

	@Override
	public LocalDateTime selectLastModifyTime(Long userId, List<Long> collectionIds) {
		if (Objects.isNull(collectionIds)) {
			LocalDateTime bookmarkTime = listAllByOwner(userId).stream()
					.map(GetBookmarkDTO::getModifyTime)
					.max(LocalDateTime::compareTo)
					.orElse(LocalDateTime.MIN);
			LocalDateTime noteTime = noteService.selectLastModifyTime(userId);
			return bookmarkTime.compareTo(noteTime) > 0 ? bookmarkTime : noteTime;
		}
		return listByCollections(userId, collectionIds, null).stream()
				.map(GetBookmarkDTO::getModifyTime)
				.max(LocalDateTime::compareTo)
				.orElse(LocalDateTime.MIN);
	}

	/*------------------------------------------< Private Service >---------------------------------------------*/

	private List<GetBookmarkDTO> listAllByOwner(Long userId) {
		Query query = MongoUtil.getEqQueryByParam("BOId", userId);
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}


	/*-------------------------------------------< Util Functions >----------------------------------------------*/

	private List<Long> detectCollectionNotExist(Long userId, List<Long> collectionIds) {
		List<Long> collectionIdList = new ArrayList<>(collectionIds);
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("COId", userId)
				.put("CId", collectionIds)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		List<Long> existCollectionIds = mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class)
				.stream().map(Collection::getId).collect(Collectors.toList());
		collectionIdList.removeAll(existCollectionIds);
		return collectionIdList;
	}

	private List<GetBookmarkDTO> getDeletedBookmarksByIds(Long userId, List<Long> bookmarkIds) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BOId", userId)
				.put("BId", bookmarkIds)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "BRTm");
		List<Bookmark> bookmarks = mongoTemplate.find(query, Bookmark.class);
		return bookmarkConvert.getBookmarkDO2DTOs(bookmarks);
	}

	private void updateToDefaultCollection(Long userId, List<Long> bookmarkIds, List<Long> collectionIds) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BId", bookmarkIds)
				.put("BOId", userId)
				.put("BCId", collectionIds)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "BRTm");
		Update update = getToDefaultUpdate();
		mongoTemplate.updateMulti(query, update, Bookmark.class);
	}

	private List<GetBookmarkDTO> getConflictBookmark(Long userId, List<GetBookmarkDTO> detectBookmarks) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("BOId", userId)
				.put("BRTm", null)
				.map();
		List<Long> bookmarkCollections = detectBookmarks.stream()
				.map(GetBookmarkDTO::getParentId)
				.collect(Collectors.toList());
		List<String> bookmarkUrls = detectBookmarks.stream()
				.map(GetBookmarkDTO::getUrl)
				.collect(Collectors.toList());
		List<String> fieldNames = List.of("BCId", "BUrl");
		Query query = MongoUtil.getEqOrQueryByMapAndList(queryMap, fieldNames, bookmarkCollections, bookmarkUrls);
		return bookmarkConvert.getBookmarkDO2DTOs(mongoTemplate.find(query, Bookmark.class));
	}

	private void detectConflictBookmark(Long userId, List<GetBookmarkDTO> detectBookmarks) {
		List<GetBookmarkDTO> conflictBookmarks = getConflictBookmark(userId, detectBookmarks);
		if (!conflictBookmarks.isEmpty()) {
			Map<String, GetBookmarkDTO> conflictUrlMap = conflictBookmarks.stream()
					.collect(Collectors.toMap(
							getBookmarkDTO -> getBookmarkDTO.getParentId() + getBookmarkDTO.getUrl(),
							getBookmarkDTO -> getBookmarkDTO,
							(oldBookmark, newBookmark) -> oldBookmark
					));
			List<EntityExistVO> conflicts = detectBookmarks.stream()
					.filter(detectBookmark -> Objects.nonNull(
							conflictUrlMap.get(detectBookmark.getParentId() + detectBookmark.getUrl())))
					.map(detectBookmark -> new EntityExistVO(
							detectBookmark.getUrl(),
							bookmarkConvert.getBookmarkDTO2VO(detectBookmark),
							bookmarkConvert.getBookmarkDTO2VO(
									conflictUrlMap.get(detectBookmark.getParentId() + detectBookmark.getUrl()))))
					.collect(Collectors.toList());
			throw new EntityExistException(ErrorCodeConstant.DATA_EXISTS_CODE, conflicts);
		}
	}

	/*----------------------------------------< Generate Update >--------------------------------------*/

	private Update getChangeCommonInfoUpdate(ChangeBookmarkDTO changeBookmarkDTO) {
		List<String> fieldNames = List.of("BTit", "BUrl", "BTag", "BDTm", "BMTm", "BDsc", "BCThn");
		List<?> fieldValues = Arrays.asList(changeBookmarkDTO.getTitle(),
				changeBookmarkDTO.getUrl(),
				changeBookmarkDTO.getTags(),
				changeBookmarkDTO.getDeleteTime(),
				changeBookmarkDTO.getModifyTime(),
				changeBookmarkDTO.getDescription(),
				changeBookmarkDTO.getCustomThumbnail());
		return MongoUtil.getUnsetUpdateWhileFieldNull(fieldNames, fieldValues);
	}

	private Update getChangeParentBookmarkUpdate(ChangeParentCollectionDTO changeParentCollectionDTO) {
		return new Update()
				.set("BCId", changeParentCollectionDTO.getToParentId())
				.set("BMTm", changeParentCollectionDTO.getModifyTime());
	}

	private Update getChangeParentFromAndToUpdate(ChangeParentCollectionDTO changeParentCollectionDTO) {
		return new Update()
				.set("CMTm", changeParentCollectionDTO.getModifyTime());
	}

	private Update getDeleteUpdate(LocalDateTime removeTime) {
		LocalDateTime now = LocalDateTime.now();
		return new Update()
				.set("BMTm", now)
				.set("BRTm", Objects.isNull(removeTime) ? now.plusDays(15L) : removeTime);
	}

	private Update getRestoreUpdate() {
		return new Update()
				.unset("BRTm")
				.set("BMTm", LocalDateTime.now());
	}
	
	private Update getTopUpdate() {
		LocalDateTime now = LocalDateTime.now();
		return new Update()
				.set("BMTm", now)
				.set("BTTm", now);
	}
	
	private Update getCancelTopUpdate() {
		return new Update()
				.unset("BTTm")
				.set("BMTm", LocalDateTime.now());
	}

	private Update getToDefaultUpdate() {
		return new Update()
				.unset("BCId")
				.set("BMTm", LocalDateTime.now());
	}

}
