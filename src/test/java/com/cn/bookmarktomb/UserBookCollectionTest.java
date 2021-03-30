package com.cn.bookmarktomb;

import com.cn.bookmarktomb.service.CollectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserBookCollectionTest {

	@Resource
	CollectionService collectionService;

	@Test
	public void TestCollection(){

	}

}
