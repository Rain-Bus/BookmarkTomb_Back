package com.cn.bookmarktomb.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author homeubuntu
 */
public class NoteDTO {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class InsertNoteDTO {

        private Long id;

        private Long ownerId;

        private Long parentId;

        private String title;

        private String content;

        private LocalDateTime createdTime;

        private LocalDateTime modifyTime;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UpdateNoteDTO {

        private Long id;

        private Long ownerId;

        private Long parentId;

        private String title;

        private String content;

        private LocalDateTime modifyTime;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class GetNoteDTO {

        private Long id;

        private Long parentId;

        private String title;

        private String content;

        private LocalDateTime createdTime;

        private LocalDateTime removeTime;

        private LocalDateTime modifyTime;

    }

    private NoteDTO(){}

    @Override
    public String toString() {
        return "";
    }
}
