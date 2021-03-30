package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.CollectionDTO.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fallen-anlgle
 */
public interface CollectionService {

    void insertCollection(InsertCollectionDTO insertCollectionDTO);

    void insertCollections(List<InsertCollectionDTO> insertCollections);

    void deleteById(Long userId, Long collectionId);

    void restoreById(Long userId, Long collectionId);

    void updateCommonInfo(CollectionCommonInfoDTO collectionCommonInfoDTO);

    void updateParent(ChangeParentDTO changeParentDTO);

    List<GetCollectionDTO> listByOwner(Long ownerId);

	List<GetCollectionDTO> listByParent(Long userId, Long parentId);

	List<GetCollectionDTO> listDeletedRootByOwner(Long userId);

    GetCollectionDTO selectById(Long userId, Long id);

	Long selectMaxId(Long userId);

	LocalDateTime selectLastModifyTime(Long userId, Long collectionId);
}
