package com.cn.bookmarktomb.controller;

import com.cn.bookmarktomb.model.convert.NoteConvert;
import com.cn.bookmarktomb.model.dto.NoteDTO.*;
import com.cn.bookmarktomb.model.vo.NoteVO.*;
import com.cn.bookmarktomb.service.NoteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author fallen-angle
 * This is controllers of note.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class NoteController {

    private final NoteConvert noteConvert;
    private final NoteService noteService;

    @PostMapping("/note")
    @ApiOperation("Insert a note")
    @ApiResponse(description = "The inserted note")
    public ResponseEntity<Object> insertNote(@ApiParam(hidden = true) Long userId,
                                             @Valid @RequestBody InsertNoteVO insertNoteVO) {
        insertNoteVO.setOwnerId(userId);
        InsertNoteDTO insertNoteDTO = noteConvert.insertNoteVO2DTO(insertNoteVO);
        insertNoteDTO.setId(noteService.selectMaxId(userId) + 1L);
        noteService.insertNote(insertNoteDTO);
        return ResponseEntity.ok(noteConvert.insert2GetNoteDTO(insertNoteDTO));
    }

    @PutMapping("/note")
    @ApiOperation("Update note info")
    @ApiResponse(description = "The updated notes")
    public ResponseEntity<Object> updateNote(@ApiParam(hidden = true) Long userId,
                                             @Valid @RequestBody UpdateNoteVO updateNoteVO) {
        updateNoteVO.setOwnerId(userId);
        UpdateNoteDTO updateNoteDTO = noteConvert.updateNoteVO2DTO(updateNoteVO);
        noteService.updateInfo(updateNoteDTO);
        return ResponseEntity.ok(noteConvert.update2GetNoteDTO(updateNoteVO));
    }

    @PutMapping("/note/restore")
    @ApiOperation("Restore notes")
    public ResponseEntity<Object> restoreNote(@ApiParam(hidden = true) Long userId,
                                              @Valid @RequestBody RestoreNoteVO restoreNoteVO) {
        noteService.restoreById(userId, restoreNoteVO.getNoteIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/note")
    @ApiOperation("Delete notes")
    public ResponseEntity<Object> deleteNote(@ApiParam(hidden = true) Long userId,
                                             @Valid @RequestBody DeleteNoteVO deleteNoteVO) {
        noteService.deleteById(userId, deleteNoteVO.getNoteIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/note/owner")
    @ApiOperation("Get user's all notes")
    public ResponseEntity<Object> getNoteByOwner(@ApiParam(hidden = true) Long userId) {
        List<GetNoteDTO> getNoteDTOs = noteService.listByOwner(userId);
        return ResponseEntity.ok(noteConvert.getNoteDTO2VOs(getNoteDTOs));
    }

    @GetMapping("/note/favor")
    @ApiOperation("Get user's favor notes")
    public ResponseEntity<Object> getFavorNote(@ApiParam(hidden = true) Long userId) {
        List<GetNoteDTO> getNoteDTOs = noteService.listFavorByOwner(userId);
        return ResponseEntity.ok(noteConvert.getNoteDTO2VOs(getNoteDTOs));
    }


    @GetMapping("/note/bookmark/{bookmarkId}")
    @ApiOperation("Get bookmark notes")
    public ResponseEntity<Object> getNoteByBookmark(@ApiParam(hidden = true) Long userId,
                                                    @PathVariable("bookmarkId") Long bookmarkId) {
        List<GetNoteDTO> getNoteDTOs = noteService.listByBookmark(userId, bookmarkId);
        return ResponseEntity.ok(noteConvert.getNoteDTO2VOs(getNoteDTOs));
    }

    @GetMapping("/note/deleted")
    @ApiOperation("Get deleted notes")
    public ResponseEntity<Object> getDeletedNoteByOwner(@ApiParam(hidden = true) Long userId) {
        List<GetNoteDTO> getNoteDTOs = noteService.listDeletedByOwner(userId);
        return ResponseEntity.ok(noteConvert.getNoteDTO2VOs(getNoteDTOs));
    }

    /**
     * @deprecated This maybe used few.
     */
    @Deprecated
    @GetMapping("/note/{noteId}")
    @ApiOperation(value = "get the note by id", notes = "This is used very few, the API will be removed in later version.")
    public ResponseEntity<Object> getNoteByNoteId(@ApiParam(hidden = true) Long userId,
                                                  @PathVariable("noteId") Long noteId) {
        GetNoteDTO getNoteDTO = noteService.selectById(userId, noteId);
        return ResponseEntity.ok(noteConvert.getNoteDTO2VO(getNoteDTO));
    }
}
