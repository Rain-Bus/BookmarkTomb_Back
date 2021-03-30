package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.BookmarkDTO.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author fallen-angle
 */
public interface BookmarkService {

	void insertBookmark(InsertBookmarkDTO insertBookmark);

	void insertBookmarks(List<InsertBookmarkDTO> insertBookmarks);

	void deleteById(Long userId, List<Long> ids);

	void deleteByCollection(Long userId, List<Long> collectionIds, LocalDateTime removeTime);

	void restoreById(Long userId, List<Long> ids);

	void restoreByCollection(Long userId, List<Long> collectionIds, LocalDateTime removeTime);

	void updateCommonInfo(ChangeBookmarkDTO changeBookmarkDTO);

	void updateParent(ChangeParentCollectionDTO changeParentCollectionDTO);

	void updateTop(Long userId, List<Long> ids);

	void updateCancelTop(Long userId, List<Long> ids);

	List<GetBookmarkDTO> listById(Long userId, List<Long> ids);

	List<GetBookmarkDTO> listByOwner(Long userId, SortAndPageDTO sortAndPageDTO);

	List<GetBookmarkDTO> listByCollection(Long userId, Long collectionId, SortAndPageDTO sortAndPageDTO);

	List<GetBookmarkDTO> listByCollections(Long userId, List<Long> collectionIds, SortAndPageDTO sortAndPageDTO);

	List<GetBookmarkDTO> listByTag(Long userId, String tagName);

	List<GetBookmarkDTO> listDeletedByOwner(Long userId);

	List<GetBookmarkDTO> listFavorByOwner(Long userId, SortAndPageDTO sortAndPageDTO);

	Map<Integer, GetBookmarkDTO> listCancelTopInfoAndIndex(Long userId, List<Long> cancelIds);

	GetBookmarkDTO selectById(Long userId, Long bookmarkId);

	Long selectUserMaxId(Long userId);

	LocalDateTime selectLastModifyTime(Long userId, List<Long> collectionIds);
}
