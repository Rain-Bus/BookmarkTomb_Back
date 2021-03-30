package com.cn.bookmarktomb;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.service.impl.CollectionServiceImpl;
import com.cn.bookmarktomb.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.RuntimeUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
class BookmarkTombApplicationTests {

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
//        ArrayList<BookCollectionInfo> bookCollectionInfos = new ArrayList<>();
//        for (int i = 1; i < 10; i++) {
//            BookCollectionInfo bookCollectionInfo = new BookCollectionInfo();
//            bookCollectionInfo.setCollectionId((long) i);
//            bookCollectionInfo.setCollectionCreatorId((i/3)+1);
//            bookCollectionInfo.setCollectionName("Test");
//            bookCollectionInfo.setCollectionCreatedTime(LocalDateTime.now());
////        bookCollectionInfo.setCollectionDeleteTime();
//            bookCollectionInfo.setCollectionLastEditTime(LocalDateTime.now());
//            bookCollectionInfo.setCollectionVisitStatus("private");
//            bookCollectionInfo.setCollectionItems(0);
//            bookCollectionInfo.setCollectionTags(new ArrayList<>());
//            bookCollectionInfos.add(bookCollectionInfo);
//        }
//
//
//        bookCollectionInfoImpl.insertCollections(bookCollectionInfos);


//        System.out.println(bookCollectionInfoImpl.deleteCollectionById(1L));


//        ArrayList<Long> deleteList = new ArrayList<>();
//        deleteList.add(7L);
//        deleteList.add(6L);
//        bookCollectionInfoImpl.deleteCollectionsByIds(deleteList);

//        BookCollectionInfo bookCollectionInfo = new BookCollectionInfo();
//        bookCollectionInfo.setCollectionId(1L);
//        bookCollectionInfo.setCollectionCreatorId(2);
//        bookCollectionInfo.setCollectionName("Test");
//        bookCollectionInfo.setCollectionCreatedTime(LocalDateTime.now());
//        bookCollectionInfo.setCollectionDeleteTime(LocalDateTime.of(2030,10,01,10,0));
//        bookCollectionInfo.setCollectionLastEditTime(LocalDateTime.now());
//        bookCollectionInfo.setCollectionVisitStatus("private");
//        bookCollectionInfo.setCollectionItems(0);
//        bookCollectionInfo.setCollectionTags(new ArrayList<>());

//        bookCollectionInfoImpl.updateCollectionCommonInfoByEntity(bookCollectionInfo);


//        bookCollectionInfoImpl.updateCollectionParentById(1L, 2L);

//        bookCollectionInfoImpl.changeCollectionVisitStatusById(1L, "team");
//        System.out.println(bookCollectionInfoImpl.selectCollectionById(1L));
//        System.out.println(bookCollectionServiceImpl.selectCollectionByUserId(2));

//		JSONObject jsonObject = JsonUtil.readJsonFile(SystemUtil.getUserInfo().getHomeDir() + "/.bookmark_tomb/conf.json");
//		System.out.println();
//		ConfigCache.initConfig();
//		System.out.println(ConfigCache.getAllConfig());
    }


}
