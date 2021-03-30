package com.cn.bookmarktomb.controller;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.BadRequestException;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.CollectionConvert;
import com.cn.bookmarktomb.model.dto.CollectionDTO.*;
import com.cn.bookmarktomb.model.vo.CollectionVO.*;
import com.cn.bookmarktomb.service.CollectionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author fallen-angle
 * This is controllers of collection.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CollectionController {

    private final CollectionService collectionService;
    private final CollectionConvert collectionConvert;

    @PostMapping("/collection")
    @ApiOperation("Insert a collection")
    @ApiResponse(description = "The Inserted collection")
    public ResponseEntity<Object> insertCollection(@ApiParam(hidden = true) Long userId,
                                       @Valid @RequestBody InsertCollectionVO insertCollectionVO) {
        if (Objects.isNull(insertCollectionVO.getServerParentId())) {
            throw new BadRequestException(ErrorCodeConstant.FORM_DATE_ERROR_CODE,
                    "The collection must have the parent collection id of server!");
        }
        insertCollectionVO.setOwnerId(userId);
        InsertCollectionDTO insertCollectionDTO = collectionConvert.insertCollectionVO2DTO(insertCollectionVO);
        insertCollectionDTO.setId(collectionService.selectMaxId(insertCollectionDTO.getOwnerId()) + 1L);
        collectionService.insertCollection(insertCollectionDTO);
        insertCollectionDTO.setParentId(insertCollectionVO.getServerParentId());
        return ResponseEntity.ok(collectionConvert.insert2GetCollectionDTO(insertCollectionDTO));
    }

    @PostMapping("/collections")
    @ApiOperation("Insert several collections")
    @ApiResponse(description = "The inserted collections")
    public ResponseEntity<Object> insertCollections(@ApiParam(hidden = true) Long userId,
                                        @Valid @RequestBody InsertCollectionVOList insertCollectionVOList){
        if (insertCollectionVOList.getInsertCollectionVOs().stream()
                .filter(insertCollectionVO -> Objects.nonNull(insertCollectionVO.getServerParentId()))
                .count() != 1) {
            throw new BadRequestException(ErrorCodeConstant.FORM_DATE_ERROR_CODE,
                    "Must have exactly one collection have the parent collection id of server!");
        }
        insertCollectionVOList.getInsertCollectionVOs()
                .forEach(insertCollection -> insertCollection.setOwnerId(userId));
        List<InsertCollectionDTO> insertCollectionDTOs =
                collectionConvert.insertCollectionVO2DTOs(insertCollectionVOList.getInsertCollectionVOs());
        List<InsertCollectionDTO> generatedIdDTOs = generateInsertCollectionIds(insertCollectionDTOs);
        collectionService.insertCollections(generatedIdDTOs);
        return ResponseEntity.ok(generatedIdDTOs);
    }

    @PutMapping("/collection")
    @ApiOperation("Update collection info")
    @ApiResponse(description = "The updated collections")
    public ResponseEntity<Object> updateCollectionCommonInfo(@ApiParam(hidden = true) Long userId,
                                                             @Valid @RequestBody CollectionCommonInfoVO collectionCommonInfoVO) {
        collectionCommonInfoVO.setOwnerId(userId);
        CollectionCommonInfoDTO collectionCommonInfoDTO = collectionConvert.collectionCommonInfoVO2DTO(collectionCommonInfoVO);
        collectionService.updateCommonInfo(collectionCommonInfoDTO);
        GetCollectionDTO getCollectionDTO = collectionService.selectById(userId, collectionCommonInfoDTO.getId());
        return ResponseEntity.ok(collectionConvert.getCollectionDTO2VO(getCollectionDTO));
    }

    @PutMapping("/collection/restore/{collectionId}")
    @ApiOperation("Restore collection")
    public ResponseEntity<Object> restoreCollections(@ApiParam(hidden = true) Long userId,
                                                    @PathVariable("collectionId") Long collectionId) {
        collectionService.restoreById(userId, collectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/collection/parent")
    @ApiOperation("Update parent collection")
    public ResponseEntity<Object> changeParentCollection(@ApiParam(hidden = true) Long userId,
                                                         @Valid @RequestBody ChangeParentVO changeParentVO) {
        changeParentVO.setOwnerId(userId);
        ChangeParentDTO changeParentDTO = collectionConvert.changeParentVO2DTO(changeParentVO);
        collectionService.updateParent(changeParentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/collection/{collectionId}")
    @ApiOperation("Delete collection")
    public ResponseEntity<Object> deleteCollections(@ApiParam(hidden = true) Long userId,
                                                    @PathVariable("collectionId") Long collectionId){
        collectionService.deleteById(userId, collectionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/collection/{id}")
    @ApiOperation("Get a collection")
    public ResponseEntity<Object> getCollection(@ApiParam(hidden = true) Long userId,
                                                @PathVariable("id") Long collectionId) {
        GetCollectionDTO getCollectionDTO = collectionService.selectById(userId, collectionId);
        return ResponseEntity.ok(collectionConvert.getCollectionDTO2VO(getCollectionDTO));
    }

    @GetMapping("/collection/owner")
    @ApiOperation("Get user's all collections")
    public ResponseEntity<Object> getUserCollections(@ApiParam(hidden = true) Long userId){
        List<GetCollectionDTO> getCollectionDTOs = collectionService.listByOwner(userId);
        return ResponseEntity.ok(collectionConvert.getCollectionDTO2VOs(getCollectionDTOs));
    }

    @GetMapping("/collection/parent/{parentId}")
    @ApiOperation(value = "Get collections by parent")
    public ResponseEntity<Object> getCollectionByParent(@ApiParam(hidden = true) Long userId,
                                                        @PathVariable("parentId") Long parentId) {
        List<GetCollectionDTO> getCollectionDTOs = collectionService.listByParent(userId, parentId);
        return ResponseEntity.ok(collectionConvert.getCollectionDTO2VOs(getCollectionDTOs));
    }

    @GetMapping("/collection/deleted")
    @ApiOperation("Get deleted collections")
    public ResponseEntity<Object> getDeleteCollectionByOwner(Long userId) {
        List<GetCollectionDTO> getCollectionDTOs = collectionService.listDeletedRootByOwner(userId);
        return ResponseEntity.ok(collectionConvert.getCollectionDTO2VOs(getCollectionDTOs));
    }

    @GetMapping("/collection/modify")
    @ApiOperation("Get latest modify time under the collection")
    public ResponseEntity<Object> getAllLastModifyTime(@ApiParam(hidden = true) Long userId) {
        return ResponseEntity.ok(collectionService.selectLastModifyTime(userId, null));
    }

    @GetMapping("/collection/modify/{id}")
    @ApiOperation("Get latest modify time of the user")
    public ResponseEntity<Object> getLastModifyTime(@ApiParam(hidden = true) Long userId,
                                                    @PathVariable("id") Long collectionId) {
        return ResponseEntity.ok(collectionService.selectLastModifyTime(userId, collectionId));
    }

    private List<InsertCollectionDTO> generateInsertCollectionIds(List<InsertCollectionDTO> insertCollectionDTOs){
        Long maxCollectionId = collectionService.selectMaxId(insertCollectionDTOs.get(0).getOwnerId());
        MapBuilder<Long, Long> idMapBuilder = MapBuilder.create();
        for (InsertCollectionDTO insertCollectionDTO: insertCollectionDTOs) {
            Long generatedId = ++maxCollectionId;
            idMapBuilder.put(insertCollectionDTO.getId(), generatedId);
            insertCollectionDTO.setId(generatedId);
        }
        Map<Long, Long> idMap = idMapBuilder.map();
        insertCollectionDTOs.forEach(insertCollectionDTO -> {
            insertCollectionDTO.setParentId(idMap.get(insertCollectionDTO.getParentId()));
            if (Objects.nonNull(insertCollectionDTO.getServerParentId())) {
                insertCollectionDTO.setParentId(insertCollectionDTO.getServerParentId());
            }
        });
        return insertCollectionDTOs;
    }
}