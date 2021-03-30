package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.NoteDTO.*;
import com.cn.bookmarktomb.model.entity.Note;
import com.cn.bookmarktomb.model.vo.NoteVO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface NoteConvert {

    @Mapping(target = "createdTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "content",
            expression = "java(cn.hutool.core.util.StrUtil.isBlank(insertNoteVO.getContent()) ? com.cn.bookmarktomb.model.constant.CommonConstant.EMPTY_STR : insertNoteVO.getContent())")
	InsertNoteDTO insertNoteVO2DTO(InsertNoteVO insertNoteVO);

    Note insertNoteDTO2DO(InsertNoteDTO insertNoteDTO);

    @Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "content",
            expression = "java(cn.hutool.core.util.StrUtil.isBlank(updateNoteVO.getContent()) ? com.cn.bookmarktomb.model.constant.CommonConstant.EMPTY_STR : updateNoteVO.getContent())")
	UpdateNoteDTO updateNoteVO2DTO(UpdateNoteVO updateNoteVO);

    GetNoteVO getNoteDTO2VO(GetNoteDTO getNoteDTO);

    GetNoteDTO getNoteDO2DTO(Note note);

    GetNoteDTO insert2GetNoteDTO(InsertNoteDTO insertNoteDTO);

    GetNoteDTO update2GetNoteDTO(UpdateNoteVO updateNoteVO);

    List<GetNoteVO> getNoteDTO2VOs(List<GetNoteDTO> getNoteDTOs);

    List<GetNoteDTO> getNoteDO2DTOs(List<Note> notes);
}
