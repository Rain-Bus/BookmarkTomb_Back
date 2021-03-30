package com.cn.bookmarktomb.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fallen-angle
 */
public class NoteVO {

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class InsertNoteVO {

//        @NotNull
        private Long parentId;

        @Null
        @ApiModelProperty(hidden = true)
        private Long ownerId;

        private String title;

        private String content;

    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class DeleteNoteVO {

		@NotEmpty
        private List<Long> noteIds;

    }

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class RestoreNoteVO {

		@NotEmpty
		private List<Long> noteIds;

	}


	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class UpdateNoteVO {

	    @NotNull
        private Long id;

        @Null
        @ApiModelProperty(hidden = true)
        private Long ownerId;

        @NotNull
        private Long parentId;

        private String title;

        private String content;

    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class GetNoteVO {

        private Long id;

        private Long parentId;

        private String title;

        private String content;

        private LocalDateTime createdTime;

        private LocalDateTime removeTime;

        private LocalDateTime modifyTime;

    }

    private NoteVO(){}

    @Override
    public String toString() {
        return "";
    }
}
