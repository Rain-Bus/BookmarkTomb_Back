package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.NoteDTO.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fallen-angle
 */
public interface NoteService {

    void insertNote(InsertNoteDTO insertNoteDTO);

    void deleteById(Long userId, List<Long> noteIds);

	void deleteByBookmark(Long userId, List<Long> bookmarkIds, LocalDateTime removeTime);

	void restoreById(Long userId, List<Long> ids);

	void restoreByBookmark(Long userId, List<Long> ids, List<LocalDateTime> removeTimes);

	void updateInfo(UpdateNoteDTO updateNoteDTO);

	List<GetNoteDTO> listByOwner(Long userId);

	List<GetNoteDTO> listByBookmark(Long userId, Long bookmarkId);

	List<GetNoteDTO> listFavorByOwner(Long userId);

	List<GetNoteDTO> listDeletedByOwner(Long userId);

	GetNoteDTO selectById(Long userId, Long noteId);

	Long selectMaxId(Long userId);

	LocalDateTime selectLastModifyTime(Long userId);
}
