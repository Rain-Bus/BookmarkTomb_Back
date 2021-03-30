package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.EntityExistException;
import com.cn.bookmarktomb.excepotion.EntityNotFoundException;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.CollectionConvert;
import com.cn.bookmarktomb.model.dto.CollectionDTO.*;
import com.cn.bookmarktomb.model.entity.Collection;
import com.cn.bookmarktomb.model.vo.EntityExistVO;
import com.cn.bookmarktomb.util.MongoUtil;
import com.cn.bookmarktomb.service.BookmarkService;
import com.cn.bookmarktomb.service.CollectionService;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fallen-anlgle
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

	private final MongoTemplate mongoTemplate;
	private final BookmarkService bookmarkService;
	private final CollectionConvert collectionConvert;

	static final Map<String, Object> NOT_FOUND_MAX_ID_MAP =
			MapBuilder.<String, Object>create().put("maxCollectionId", 0L).map();

	/*-------------------------------------------< Service >----------------------------------------------*/
	// All the service firstly get the query, and then if need get the update, finally exec the operation
	// If the operation is involved in the specified amount of data, will judge the matched data of db is same as the input or not

	@Override
	public void insertCollection(InsertCollectionDTO insertCollectionDTO) {
		if (selectById(insertCollectionDTO.getOwnerId(), insertCollectionDTO.getServerParentId()) == null) {
			insertCollectionDTO.setServerParentId(0L);
		}
		GetCollectionDTO getCollectionDTO = collectionConvert.insert2GetCollectionDTO(insertCollectionDTO);
		getCollectionDTO.setParentId(insertCollectionDTO.getServerParentId());
		detectConflictCollection(insertCollectionDTO.getOwnerId(), getCollectionDTO);
		insertCollectionDTO.setParentId(insertCollectionDTO.getServerParentId());
		mongoTemplate.insert(collectionConvert.insertCollectionDTO2DO(insertCollectionDTO));
	}

	@Override
	public void insertCollections(List<InsertCollectionDTO> insertCollectionDTOs) {
		InsertCollectionDTO rootCollection = insertCollectionDTOs.stream()
				.filter(insertCollectionDTO -> Objects.nonNull(insertCollectionDTO.getServerParentId()))
				.findFirst()
				.orElseThrow(() -> new SystemException("Can't get the collection in stream!"));
		if (selectById(rootCollection.getOwnerId(), rootCollection.getServerParentId()) == null) {
			rootCollection.setServerParentId(0L);
		}
		GetCollectionDTO rootCollectionDTO = collectionConvert.insert2GetCollectionDTO(rootCollection);
		rootCollectionDTO.setParentId(rootCollection.getServerParentId());
		detectConflictCollection(rootCollection.getOwnerId(), rootCollectionDTO);
		mongoTemplate.insertAll(collectionConvert.insertCollectionDTO2DOs(insertCollectionDTOs));
	}

	@Override
	public void deleteById(Long userId, Long collectionId) {
		// Traverse all collections under this collection
		List<Long> deleteCollectionIds = traversCollectionsByParentId(userId, collectionId, null).stream()
				.map(GetCollectionDTO::getId)
				.collect(Collectors.toList());

		// Set the Filed "CRTm" for the collection(haven't been deleted) to be delete.
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CId", deleteCollectionIds)
				.put("CId", deleteCollectionIds)
				.put("CRTm", null)
				.put("COId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		LocalDateTime removeTime = LocalDateTime.now().plusDays(15L);
		Update update = getDeleteUpdate(removeTime);
		mongoTemplate.updateMulti(query, update, com.cn.bookmarktomb.model.entity.Collection.class);

		// The collection's remove time will be used to delete bookmarks and notes in the collection.
		bookmarkService.deleteByCollection(userId, deleteCollectionIds, removeTime);
	}

	@Override
	public void restoreById(Long userId, Long collectionId) {
		// Get the need to be restored collection's remove times, which will be used when query the cascading deleted bookmarks and notes.
		GetCollectionDTO rootCollection =
				Optional.of(getDeletedCollectionsByIds(userId, Collections.singletonList(collectionId)))
						.map(collections -> collections.get(0))
						.orElseThrow(() -> new EntityNotFoundException("Can't found deleted collection: " + collectionId));
		detectConflictCollection(userId, rootCollection);
		LocalDateTime removeTime = rootCollection.getRemoveTime();
		List<Long> restoreCollectionIds = traversCollectionsByParentId(userId, collectionId, removeTime).stream()
				.map(GetCollectionDTO::getId)
				.collect(Collectors.toList());
		bookmarkService.restoreByCollection(userId, restoreCollectionIds, removeTime);

		// Get the restore collection's parent collection,
		// if the parent collection is not exist(have "CRTm" or not exist in DB) and not exist in the need to be restored collection ids,
		// will set the parent collection as null, and at last restore the collection(unset the collection's "CRTm")
		if (detectCollectionExist(userId,rootCollection.getParentId())){
			updateParentCollectionToNullById(userId, collectionId);
		}
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CId", restoreCollectionIds)
				.put("COId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "CRTm");
		Update update = getRestoreUpdate();
		if (mongoTemplate.updateMulti(query, update, com.cn.bookmarktomb.model.entity.Collection.class).getMatchedCount() < restoreCollectionIds.size()) {
			throw new EntityNotFoundException("Some collection not found when restore " + collectionId);
		}
	}

	@Override
	public void updateCommonInfo(CollectionCommonInfoDTO collectionCommonInfoDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("CId", collectionCommonInfoDTO.getId())
				.put("COId", collectionCommonInfoDTO.getOwnerId())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getCollectionCommonInfoUpdate(collectionCommonInfoDTO);
		if (mongoTemplate.updateFirst(query, update, com.cn.bookmarktomb.model.entity.Collection.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("The collection not found when update " + collectionCommonInfoDTO.getId());
		}

	}

	@Override
	public void updateParent(ChangeParentDTO changeParentDTO) {

		// Get the from and to collection query and update.
		List<Long> fromAndToCollectionIds = new ArrayList<>(2);
		fromAndToCollectionIds.add(changeParentDTO.getFromParentId());
		fromAndToCollectionIds.add(changeParentDTO.getToParentId());
		Map<String, Object> fromAndToQueryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("CId", fromAndToCollectionIds)
				.put("COId", changeParentDTO.getOwnerId())
				.map();
		Query fromAndToQuery = MongoUtil.getEqQueryByMap(fromAndToQueryMap);
		Update fromAndToUpdate = getChangeParentFromAndToUpdate(changeParentDTO);
		// Firstly, modify the last edit time, and detect the collection is exist or not.
		if (mongoTemplate.updateMulti(fromAndToQuery, fromAndToUpdate, com.cn.bookmarktomb.model.entity.Collection.class).getModifiedCount() < fromAndToCollectionIds.size()) {
			throw new EntityNotFoundException("The collection not found when update parents " + fromAndToCollectionIds);
		}

		// Get the need change collections' query and update.
		Map<String, Object> changeQueryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("COId", changeParentDTO.getOwnerId())
				.put("CId", changeParentDTO.getId())
				.put("PCId", changeParentDTO.getFromParentId())
				.map();
		Query changeQuery = MongoUtil.getEqQueryByMap(changeQueryMap);
		Update changeUpdate = getChangeParentUpdate(changeParentDTO);
		mongoTemplate.updateMulti(changeQuery, changeUpdate, com.cn.bookmarktomb.model.entity.Collection.class);
	}

	@Override
	public GetCollectionDTO selectById(Long userId, Long id) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CId", id)
				.put("CRTm", null)
				.put("COId",userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		List<com.cn.bookmarktomb.model.entity.Collection> collections = mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class);
		com.cn.bookmarktomb.model.entity.Collection collection = collections.isEmpty() ? null : collections.get(0);
		return collectionConvert.getCollectionDO2DTO(collection);
	}

	@Override
	public List<GetCollectionDTO> listByOwner(Long userId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("COId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap)
				.with(Sort.by(Sort.Direction.DESC, "PCId", "CId"));
		List<com.cn.bookmarktomb.model.entity.Collection> collections = mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class);
		return collectionConvert.getCollectionDO2DTOs(collections);
	}

	@Override
	public List<GetCollectionDTO> listByParent(Long userId, Long parentId) {
		return traversCollectionsByParentId(userId, parentId, null);
	}

	@Override
	public List<GetCollectionDTO> listDeletedRootByOwner(Long userId) {
		return filterOnlyRoot(listDeletedByOwner(userId));
	}

	@Override
	public Long selectMaxId(Long userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("COId").is(userId)),
				Aggregation.group("COId").max("CId").as("maxCollectionId")
		);
		Map<String, Object> resultMap =
				mongoTemplate.aggregate(aggregation, "collection_info", BasicDBObject.class)
						.getUniqueMappedResult();
		resultMap = Optional.ofNullable(resultMap)
				.orElse(NOT_FOUND_MAX_ID_MAP);
		return (Long)Optional.ofNullable(resultMap.get("maxCollectionId"))
				.orElse(0L);
	}

	@Override
	public LocalDateTime selectLastModifyTime(Long userId, Long collectionId) {
		if (Objects.isNull(collectionId)) {
			LocalDateTime collectionTime = listAllByOwner(userId).stream()
					.map(GetCollectionDTO::getModifyTime)
					.max(LocalDateTime::compareTo)
					.orElse(LocalDateTime.MIN);
			LocalDateTime bookmarkTime = bookmarkService.selectLastModifyTime(userId, null);
			return collectionTime.compareTo(bookmarkTime) > 0 ? collectionTime : bookmarkTime;
		}
		List<GetCollectionDTO> collections = traversCollectionsByParentId(userId, collectionId, LocalDateTime.MIN);
		List<Long> collectionIds = collections.stream()
				.map(GetCollectionDTO::getId)
				.collect(Collectors.toList());
		LocalDateTime collectionTime = collections.stream()
				.map(GetCollectionDTO::getModifyTime)
				.max(LocalDateTime::compareTo)
				.orElse(LocalDateTime.MIN);
		LocalDateTime bookmarkTime = bookmarkService.selectLastModifyTime(userId, collectionIds);
		return collectionTime.compareTo(bookmarkTime) > 0 ? collectionTime : bookmarkTime;
	}

	/*------------------------------------------< Private Service >---------------------------------------------*/

	private List<GetCollectionDTO> listAllByOwner(Long userId) {
		Query query = MongoUtil.getEqQueryByParam("COId", userId);
		return collectionConvert.getCollectionDO2DTOs(mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class));
	}

	public List<GetCollectionDTO> listDeletedByOwner(Long userId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("COId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "CRTm");
		query.with(Sort.by(Sort.Direction.DESC, "CRTm"));
		List<com.cn.bookmarktomb.model.entity.Collection> collections = mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class);
		return collectionConvert.getCollectionDO2DTOs(collections);
	}

	/*-------------------------------------------< Util Functions >----------------------------------------------*/

	private List<GetCollectionDTO> getDeletedCollectionsByIds(Long userId, List<Long> collectionIds) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("COId", userId)
				.put("CId", collectionIds)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "CRTm");
		List<com.cn.bookmarktomb.model.entity.Collection> collections = mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class);
		return collectionConvert.getCollectionDO2DTOs(collections);
	}

	private boolean detectCollectionExist(Long userId, Long collectionIds) {
		// Get the not deleted collection's, the remove the exist collections, can get the not exist collections.
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("COId", userId)
				.put("CId", collectionIds)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		return mongoTemplate.find(query, com.cn.bookmarktomb.model.entity.Collection.class).isEmpty();
	}

	private void updateParentCollectionToNullById(Long userId, Long collectionId) {
		Map<String,Object> queryMap = MapBuilder.<String, Object>create()
				.put("CId", collectionId)
				.put("COId", userId)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap,"CRTm");
		Update update = getNullParentUpdate();
		mongoTemplate.updateMulti(query, update, com.cn.bookmarktomb.model.entity.Collection.class);
	}

	private List<GetCollectionDTO> traversCollectionsByParentId(Long userId, Long parentCollectionId, LocalDateTime modeFlag) {
		List<GetCollectionDTO> subCollections = new ArrayList<>();
		List<GetCollectionDTO> allCollections;

		// The modeFlag is the removeTime, which will be used to distinguish the mode.
		// 		1. null					: This is the travers of not deleted collection.
		//		2. LocalDateTime.MIN	: This is the travers of all collections.
		// 		3. other				: This is the travers of deleted collections.
		if (Objects.isNull(modeFlag)) {
			allCollections = listByOwner(userId);
		} else if (LocalDateTime.MIN.equals(modeFlag)) {
			allCollections = listAllByOwner(userId);
		} else {
			allCollections = listDeletedByOwner(userId).stream()
					.filter(getCollectionDTO -> modeFlag.equals(getCollectionDTO.getRemoveTime()))
					.collect(Collectors.toList());
		}

		// Traverse the tree.
		Queue<GetCollectionDTO> currentCollections = new LinkedList<>();
		currentCollections.add(
				allCollections.stream()
						.filter(getCollectionDTO -> parentCollectionId.equals(getCollectionDTO.getId()))
						.findFirst()
						.orElseThrow(() -> new EntityNotFoundException("Can't found collection: " + parentCollectionId)));
		while (!currentCollections.isEmpty()) {
			GetCollectionDTO currentCollection = currentCollections.remove();
			currentCollections.addAll(
					allCollections.stream()
							.filter(getCollectionDTO -> currentCollection.getId().equals(getCollectionDTO.getParentId()))
							.collect(Collectors.toList()));
			subCollections.add(currentCollection);
		}

		return subCollections;
	}

	private List<GetCollectionDTO> getConflictCollection(Long userId, Long parentId, String collectionName) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("CRTm", null)
				.put("COId", userId)
				.put("PCId", parentId)
				.put("CName", collectionName)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		return collectionConvert.getCollectionDO2DTOs(mongoTemplate.find(query, Collection.class));
	}

	private void detectConflictCollection(Long userId, GetCollectionDTO sourceCollection) {
		List<GetCollectionDTO> conflictCollections = getConflictCollection(
				userId,
				sourceCollection.getParentId(),
				sourceCollection.getTitle());
		if (!conflictCollections.isEmpty()) {
			GetCollectionDTO conflictCollection = conflictCollections.get(0);
			EntityExistVO entityExist = new EntityExistVO(
					conflictCollection.getTitle(),
					collectionConvert.getCollectionDTO2VO(sourceCollection),
					collectionConvert.getCollectionDTO2VO(conflictCollection)
			);
			throw new EntityExistException(ErrorCodeConstant.DATA_EXISTS_CODE, entityExist);
		}
	}

	private List<GetCollectionDTO> filterOnlyRoot(List<GetCollectionDTO> collections){
		Map<LocalDateTime, List<Long>> idMap = collections.stream().collect(Collectors.toMap(
				GetCollectionDTO::getRemoveTime,
				collection -> ListUtil.toList(collection.getId()),
				(oldVal, newVal) -> {oldVal.addAll(newVal); return oldVal;}
		));
		Map<LocalDateTime, List<Long>> parentIdMap = collections.stream().collect(Collectors.toMap(
				GetCollectionDTO::getRemoveTime,
				collection -> ListUtil.toList(collection.getParentId()),
				(oldVal, newVal) -> {oldVal.addAll(newVal); return oldVal;}
		));
		parentIdMap.forEach((time, ids) -> ids.removeAll(idMap.get(time)));
		return collections.stream()
				.filter(collection -> parentIdMap.get(collection.getRemoveTime()).contains(collection.getParentId()))
				.collect(Collectors.toList());
	}

	/*----------------------------------------< Generate Update >--------------------------------------*/

	private Update getCollectionCommonInfoUpdate(CollectionCommonInfoDTO collectionCommonInfoDTO) {
		List<String> fieldNames = List.of("CName", "CDTm", "CMTm", "CDsc");
		List<?> fieldValues = Arrays.asList(collectionCommonInfoDTO.getTitle(),
				collectionCommonInfoDTO.getDeleteTime(),
				collectionCommonInfoDTO.getModifyTime(),
				collectionCommonInfoDTO.getDescription());
		return MongoUtil.getUnsetUpdateWhileFieldNull(fieldNames, fieldValues);
	}

	private Update getChangeParentUpdate(ChangeParentDTO changeParentDTO) {
		return new Update()
				.set("PCId", changeParentDTO.getToParentId())
				.set("CMTm", changeParentDTO.getModifyTime());
	}

	private Update getChangeParentFromAndToUpdate(ChangeParentDTO changeParentDTO) {
		return new Update()
				.set("CMTm", changeParentDTO.getModifyTime());
	}

	private Update getDeleteUpdate(LocalDateTime removeTime) {
		LocalDateTime now = LocalDateTime.now();
		return new Update()
				.set("CMTm", now)
				.set("CRTm", Objects.isNull(removeTime) ? now.plusDays(15) : removeTime);
	}

	private Update getRestoreUpdate() {
		return new Update()
				.unset("CRTm")
				.set("CMTm", LocalDateTime.now());
	}

	private Update getNullParentUpdate(){
		return new Update()
				.set("PCId", 0L)
				.set("CMTm", LocalDateTime.now());
	}

}