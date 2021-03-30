package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.EntityNotFoundException;
import com.cn.bookmarktomb.model.convert.NoteConvert;
import com.cn.bookmarktomb.model.dto.NoteDTO.*;
import com.cn.bookmarktomb.model.entity.Bookmark;
import com.cn.bookmarktomb.model.entity.Note;
import com.cn.bookmarktomb.util.MongoUtil;
import com.cn.bookmarktomb.service.NoteService;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
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
public class NoteServiceImpl implements NoteService {

    private final NoteConvert noteConvert;
    private final MongoTemplate mongoTemplate;

    static final Map<String, Object> NOT_FOUND_MAX_ID_MAP =
            MapBuilder.<String, Object>create().put("maxNoteId", 0L).map();

    /*-------------------------------------------< Service >----------------------------------------------*/
    // All the service firstly get the query, and then if need get the update, finally exec the operation
    // If the operation is involved in the specified amount of data, will judge the matched data of db is same as the input or not

    @Override
    public void insertNote(InsertNoteDTO insertNoteDTO) {
        if (!detectBookmarkNotExist(insertNoteDTO.getOwnerId(), Collections.singletonList(insertNoteDTO.getParentId())).isEmpty()) {
            insertNoteDTO.setParentId(null);
        }
        mongoTemplate.insert(noteConvert.insertNoteDTO2DO(insertNoteDTO));
    }

    @Override
    public void deleteById(Long userId, List<Long> ids) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NId", ids)
                .put("NRTm", null)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        Update update = getDeleteUpdate(null);
        if (mongoTemplate.updateMulti(query, update, Note.class).getMatchedCount() < ids.size()) {
            throw new EntityNotFoundException("Some note not found when delete " + ids);
        }
    }

    @Override
    public void deleteByBookmark(Long userId, List<Long> bookmarkIds, LocalDateTime removeTime) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NOId", userId)
                .put("NBId", bookmarkIds)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        Update update = getDeleteUpdate(removeTime);
        mongoTemplate.updateMulti(query, update, Note.class);
    }

    @Override
    public void restoreById(Long userId, List<Long> ids) {
        // Get these notes parent bookmark, and detect the bookmark is exist or not, and set these note to default bookmark(my notes).
        List<Long> bookmarkIds = getDeletedNotesByIds(userId, ids)
                .stream().map(GetNoteDTO::getParentId).distinct().collect(Collectors.toList());
        List<Long> notExistBookmark = detectBookmarkNotExist(userId, bookmarkIds);
        updateNoteToDefaultBookmark(userId, ids, notExistBookmark);
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NId", ids)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "NRTm");
        Update update = getRestoreUpdate();
        mongoTemplate.updateMulti(query, update, Note.class).getMatchedCount();
//        if (mongoTemplate.updateMulti(query, update, Note.class).getMatchedCount() < ids.size()) {
//            throw new EntityNotFoundException("Some note not found when restore " + ids);
//        }
    }

    @Override
    public void restoreByBookmark(Long userId, List<Long> bookmarkIds, List<LocalDateTime> removeTimes) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NOId", userId)
                .put("NRTm", removeTimes)
                .put("NBId", bookmarkIds)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        Update update = getRestoreUpdate();
        mongoTemplate.updateMulti(query, update, Note.class);
    }

    @Override
    public void updateInfo(UpdateNoteDTO updateNoteDTO) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NId", updateNoteDTO.getId())
                .put("NOId", updateNoteDTO.getOwnerId())
                .put("NBId", updateNoteDTO.getParentId())
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        Update update = getNoteUpdate(updateNoteDTO);
        if (mongoTemplate.updateFirst(query, update, Note.class).getMatchedCount() < 1) {
            throw new EntityNotFoundException("Some note not found when update " + updateNoteDTO.getId());
        }
    }

    @Override
    public List<GetNoteDTO> listByOwner(Long userId) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap)
                .with(Sort.by(Sort.Direction.DESC, "NCTm"));
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    @Override
    public List<GetNoteDTO> listByBookmark(Long userId, Long bookmarkId) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NOId", userId)
                .put("NBId", bookmarkId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap)
                .with(Sort.by(Sort.Direction.DESC, "NCTm"));
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    @Override
    public List<GetNoteDTO> listFavorByOwner(Long userId) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NBId", null)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap)
                .with(Sort.by(Sort.Direction.DESC, "NCTm"));
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    @Override
    public List<GetNoteDTO> listDeletedByOwner(Long userId) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "NRTm")
                .with(Sort.by(Sort.Direction.DESC, "NRTm"));
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    @Override
    public GetNoteDTO selectById(Long userId, Long noteId) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NId", noteId)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        Note note = mongoTemplate.findOne(query, Note.class);
        return noteConvert.getNoteDO2DTO(note);
    }

    @Override
    public Long selectMaxId(Long userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("NOId").is(userId)),
                Aggregation.group("NOId").max("NId").as("maxNoteId")
        );
        Map<String, Object> resultMap =
                mongoTemplate.aggregate(aggregation, "note_info", BasicDBObject.class)
                    .getUniqueMappedResult();
        resultMap = Optional.ofNullable(resultMap)
                .orElse(NOT_FOUND_MAX_ID_MAP);
        return (Long)Optional.ofNullable(resultMap.get("maxNoteId"))
                .orElse(0L);
    }

    @Override
    public LocalDateTime selectLastModifyTime(Long userId) {
        return listAllByOwner(userId).stream()
                .map(GetNoteDTO::getModifyTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
    }

    /*------------------------------------------< Private Service >---------------------------------------------*/

    private List<GetNoteDTO> listAllByOwner(Long userId) {
        Query query = MongoUtil.getEqQueryByParam("NOId", userId);
        return noteConvert.getNoteDO2DTOs(mongoTemplate.find(query, Note.class));
    }

    /*-------------------------------------------< Util Functions >----------------------------------------------*/

    private List<GetNoteDTO> getDeletedNotesByIds(Long userId, List<Long> ids) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NId", ids)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap,"NRTm");
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    private void updateNoteToDefaultBookmark(Long userId, List<Long> noteIds, List<Long> notExistBookmarkIds) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NId", noteIds)
                .put("NBId", notExistBookmarkIds)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "NRTm");
        Update update = getToDefaultParentUpdate();
        mongoTemplate.updateMulti(query, update, Note.class);
    }

    private List<GetNoteDTO> getNoteByIds(Long userId, List<Long> noteIds) {
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("NRTm", null)
                .put("NId", noteIds)
                .put("NOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        List<Note> notes = mongoTemplate.find(query, Note.class);
        return noteConvert.getNoteDO2DTOs(notes);
    }

    private List<Long> detectBookmarkNotExist(Long userId, List<Long> bookmarkIds) {
        List<Long> bookmarkIdList = new ArrayList<>(bookmarkIds);
        Map<String, Object> queryMap = MapBuilder.<String, Object>create()
                .put("BId", bookmarkIds)
                .put("BRTm", null)
                .put("BOId", userId)
                .map();
        Query query = MongoUtil.getEqQueryByMap(queryMap);
        List<Bookmark> bookmarks = mongoTemplate.find(query, Bookmark.class);
        List<Long> existIds = bookmarks.stream().map(Bookmark::getId).collect(Collectors.toList());
        bookmarkIdList.removeAll(existIds);
        return bookmarkIdList;
    }


    /*----------------------------------------< Generate Update >--------------------------------------*/

    private Update getNoteUpdate(UpdateNoteDTO updateNoteDTO) {
        return new Update()
                .set("NTit", updateNoteDTO.getTitle())
                .set("NCont", updateNoteDTO.getContent())
                .set("NMTm", updateNoteDTO.getModifyTime());
    }

    private Update getDeleteUpdate(LocalDateTime removeTime) {
        LocalDateTime now = LocalDateTime.now();
        return new Update()
                .set("NMTm", now)
                .set("NRTm", Objects.isNull(removeTime) ? now.plusDays(15) : removeTime);
    }

    private Update getRestoreUpdate() {
        return new Update()
                .unset("NRTm")
                .set("NMTm", LocalDateTime.now());
    }

    private Update getToDefaultParentUpdate() {
        return new Update()
                .unset("NBId")
                .set("NMTm", LocalDateTime.now());
    }

}