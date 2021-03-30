package com.cn.bookmarktomb.controller;

import cn.hutool.core.map.MapUtil;
import com.cn.bookmarktomb.model.convert.BookmarkConvert;
import com.cn.bookmarktomb.model.dto.BookmarkDTO.*;
import com.cn.bookmarktomb.model.vo.BookmarkVO.*;
import com.cn.bookmarktomb.service.BookmarkService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fallen-angle
 * This is controllers of bookmark.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class BookmarkController {

	private final BookmarkConvert bookmarkConvert;
	private final BookmarkService bookmarkService;

	@PostMapping("/bookmark")
	@ApiOperation("Insert a bookmark")
	@ApiResponse(description = "The inserted bookmark")
	public ResponseEntity<Object> insertOne(@ApiParam(hidden = true) Long userId,
											@Valid @RequestBody InsertBookmarkVO insertBookmarkVO) {
		insertBookmarkVO.setOwnerId(userId);
		InsertBookmarkDTO insertBookmarkDTO = bookmarkConvert.insertBookmarkVO2DTO(insertBookmarkVO);
		insertBookmarkDTO.setId(bookmarkService.selectUserMaxId(insertBookmarkDTO.getOwnerId()) + 1L);
		bookmarkService.insertBookmark(insertBookmarkDTO);
		return ResponseEntity.ok(bookmarkConvert.insert2GetBookmarkDTO(insertBookmarkDTO));
	}

	@PostMapping("/bookmarks")
	@ApiOperation("Insert several bookmarks")
	@ApiResponse(description = "The inserted bookmarks")
	public ResponseEntity<Object> insertSeveral(@ApiParam(hidden = true) Long userId,
											@Valid @RequestBody InsertBookmarkVOList insertBookmarkVOList) {
		// Remove the same name and url bookmark, distinct by Map.
		List<InsertBookmarkVO> insertBookmarkVOs = new ArrayList<>(insertBookmarkVOList.getInsertBookmarkVOs().stream()
				.collect(Collectors.toMap(
						insertBookmarkVO -> insertBookmarkVO.getParentId() + insertBookmarkVO.getUrl(),
						insertBookmarkVO -> insertBookmarkVO,
						(oldBookmark, newBookmark) -> oldBookmark))
				.values());

		insertBookmarkVOs.forEach(insertBookmarkVO -> insertBookmarkVO.setOwnerId(userId));
		List<InsertBookmarkDTO> insertBookmarkDTOs = bookmarkConvert.insertBookmarkVO2DTOs(insertBookmarkVOs);

		// Set bookmark id for each to be insert
		Long maxBookmarkId = bookmarkService.selectUserMaxId(insertBookmarkDTOs.get(0).getOwnerId());
		for (InsertBookmarkDTO insertBookmarkDTO: insertBookmarkDTOs) {
			insertBookmarkDTO.setId(++maxBookmarkId);
		}

		bookmarkService.insertBookmarks(insertBookmarkDTOs);
		return ResponseEntity.ok(bookmarkConvert.insert2GetBookmarkDTOs(insertBookmarkDTOs));
	}

	@PutMapping("/bookmark")
	@ApiOperation("Update bookmark info")
	@ApiResponse(description = "The updated bookmark")
	public ResponseEntity<Object> updateCommonInfo(@ApiParam(hidden = true) Long userId,
												   @Valid @RequestBody ChangeBookmarkVO changeBookmarkVO) {
		changeBookmarkVO.setOwnerId(userId);
		ChangeBookmarkDTO changeBookmarkDTO = bookmarkConvert.changeBookmarkVO2DTO(changeBookmarkVO);
		bookmarkService.updateCommonInfo(changeBookmarkDTO);
		GetBookmarkDTO getBookmarkDTO = bookmarkService.selectById(userId, changeBookmarkVO.getId());
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VO(getBookmarkDTO));
	}

	@PutMapping("/bookmark/restore")
	@ApiOperation("Restore bookmarks")
	public ResponseEntity<Object> restoreBookmarks(@ApiParam(hidden = true) Long userId,
												   @Valid @RequestBody RestoreBookmarkVO restoreBookmarkVO) {
		bookmarkService.restoreById(userId, restoreBookmarkVO.getBookmarkIds());
		return ResponseEntity.ok().build();
	}

	@PutMapping("/bookmark/parent")
	@ApiOperation("Update parent collection")
	public ResponseEntity<Object> updateParentCollection(@ApiParam(hidden = true) Long userId,
														 @Valid @RequestBody ChangeParentCollectionVO changeParentCollectionVO) {
		changeParentCollectionVO.setOwnerId(userId);
		ChangeParentCollectionDTO changeParentCollectionDTO = bookmarkConvert.changeParentCollectionVO2DTO(changeParentCollectionVO);
		bookmarkService.updateParent(changeParentCollectionDTO);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/bookmark/top")
	@ApiOperation("Top the bookmarks")
	@ApiResponse(description = "The topped bookmarks")
	public ResponseEntity<Object> updateTop(@ApiParam(hidden = true) Long userId,
											@Valid @RequestBody TopBookmarkVO topBookmarkVO) {
		bookmarkService.updateTop(userId, topBookmarkVO.getBookmarkIds());
		return ResponseEntity.ok(bookmarkService.listById(userId, topBookmarkVO.getBookmarkIds()));
	}

	@DeleteMapping("/bookmark/top/cancel")
	@ApiOperation("Cancel topped bookmarks")
	@ApiResponse(description = "The cancel topped bookmarks and index")
	public ResponseEntity<Object> updateCancelTop(@ApiParam(hidden = true) Long userId,
												  @Valid @RequestBody CancelTopBookmarkVO cancelTopBookmarkVO) {
		bookmarkService.updateCancelTop(userId, cancelTopBookmarkVO.getBookmarkIds());

		Map<Integer, GetBookmarkDTO> indexAndBookmarkDTOMap =
				bookmarkService.listCancelTopInfoAndIndex(userId, cancelTopBookmarkVO.getBookmarkIds());
		Map<Integer, GetBookmarkVO> indexAndBookmarkVOMap = new HashMap<>(indexAndBookmarkDTOMap.size());
		indexAndBookmarkDTOMap.forEach((index,getBookmarkDTO) ->
				indexAndBookmarkVOMap.put(index, bookmarkConvert.getBookmarkDTO2VO(getBookmarkDTO))
		);
		return ResponseEntity.ok(MapUtil.sort(indexAndBookmarkVOMap));
	}

	@DeleteMapping("/bookmark")
	@ApiOperation("Delete bookmarks")
	public ResponseEntity<Object> deleteBookmarks(@ApiParam(hidden = true) Long userId,
												  @Valid @RequestBody DeleteBookmarkVO deleteBookmarkVO) {
		bookmarkService.deleteById(userId, deleteBookmarkVO.getBookmarkIds());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/bookmark/owner")
	@ApiOperation("Get user's all bookmarks")
	public ResponseEntity<Object> getBookmarkByOwner(@ApiParam(hidden = true) Long userId,
													 @Valid SortAndPageVO sortAndPageVO) {
		SortAndPageDTO sortAndPageDTO = bookmarkConvert.sortAndPageVO2DTO(sortAndPageVO);
		List<GetBookmarkDTO> getBookmarkDTOs = bookmarkService.listByOwner(userId, sortAndPageDTO);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@GetMapping("/bookmark/favor")
	@ApiOperation("Get user's favor bookmarks")
	public ResponseEntity<Object> getBookmarkFavor(@ApiParam(hidden = true) Long userId,
												   @Valid SortAndPageVO sortAndPageVO) {
		SortAndPageDTO sortAndPageDTO = bookmarkConvert.sortAndPageVO2DTO(sortAndPageVO);
		List<GetBookmarkDTO> getBookmarkDTOs = bookmarkService.listFavorByOwner(userId, sortAndPageDTO);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@GetMapping("/bookmark/collection/{collectionId}")
	@ApiOperation("Get bookmarks under collection")
	public ResponseEntity<Object> getBookmarkByCollection(@ApiParam(hidden = true) Long userId,
														  @PathVariable("collectionId") Long collectionId,
														  @Valid SortAndPageVO sortAndPageVO) {
		SortAndPageDTO sortAndPageDTO = bookmarkConvert.sortAndPageVO2DTO(sortAndPageVO);
		List<GetBookmarkDTO> getBookmarkDTOs = bookmarkService.listByCollection(userId, collectionId, sortAndPageDTO);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@PostMapping("/bookmark/collections")
	@ApiOperation(value = "Get bookmarks under collections")
	public ResponseEntity<Object> getBookmarkByCollections(@ApiParam(hidden = true) Long userId,
														   @RequestBody GetBookmarkIdVoList getBookmarkIdVoList,
														   @Valid SortAndPageVO sortAndPageVO) {
		SortAndPageDTO sortAndPageDTO = bookmarkConvert.sortAndPageVO2DTO(sortAndPageVO);
		List<GetBookmarkDTO> getBookmarkDTOs =
				bookmarkService.listByCollections(userId, getBookmarkIdVoList.getGetBookmarkIds(), sortAndPageDTO);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@GetMapping("/bookmark/tag/{tagName}")
	@ApiOperation(value = "Get bookmark by tag")
	public ResponseEntity<Object> getBookmarkByTag(@ApiParam(hidden = true) Long userId,
												   @PathVariable("tagName") String tagName) {
		List<GetBookmarkDTO> getBookmarkDTOs = bookmarkService.listByTag(userId, tagName);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@GetMapping("/bookmark/deleted")
	@ApiOperation(value = "Get the deleted bookmarks")
	public ResponseEntity<Object> getBookmarkByOwnerByOwner(@ApiParam(hidden = true) Long userId) {
		List<GetBookmarkDTO> getBookmarkDTOs = bookmarkService.listDeletedByOwner(userId);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VOs(getBookmarkDTOs));
	}

	@Deprecated
	@GetMapping("/bookmark/{bookmarkId}")
	@ApiOperation(value = "get a bookmark by id")
	public ResponseEntity<Object> getBookmarkById(@ApiParam(hidden = true) Long userId,
												  @PathVariable("bookmarkId") Long bookmarkId) {
		GetBookmarkDTO getBookmarkDTO = bookmarkService.selectById(userId, bookmarkId);
		return ResponseEntity.ok(bookmarkConvert.getBookmarkDTO2VO(getBookmarkDTO));
	}

}