package com.cn.bookmarktomb.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fallen-anlgle
 */
@Data
@NoArgsConstructor
@Document(collection = "collection_info")
public class Collection implements Serializable {

    @Field("CId")
    private Long id;

    @Field("PCId")
    private Long parentId;

    @Field("COId")
    private Long ownerId;

    @Field("CName")
    private String title;

    @Field("CCTm")
    private LocalDateTime createdTime;

    @Field("CDTm")
    private LocalDateTime deleteTime;

    @Field("CRTm")
    private LocalDateTime removeTime;

    @Field("CMTm")
    private LocalDateTime modifyTime;

    @Field("CDsc")
    private String description;

}