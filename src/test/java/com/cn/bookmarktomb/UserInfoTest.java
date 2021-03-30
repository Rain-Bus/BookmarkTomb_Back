package com.cn.bookmarktomb;

import com.cn.bookmarktomb.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserInfoTest {

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    UserInfoService userInfoService;

    @Test
    void UserTest() throws NoSuchMethodException, NoSuchFieldException {
//        System.out.println(userInfoMapper.selectUserBasicUserInfo(Wrappers.<UserInfoDO>lambdaQuery().eq(UserInfoDO::getUserId, 43)));
//        ResetEmailDTO resetEmailDTO = new ResetEmailDTO();
//        resetEmailDTO.setUserEmail("12@qq.com.cn");
//        resetEmailDTO.setUserId(3L);
//        resetEmailDTO.setUserOldEmail("1@qq.com.cn");
//        userInfoService.updateUserEmailByResetEmailDTO(resetEmailDTO);
//        Method method = UserInfoDO.class.getMethod("setUserId", Long.class);
//        System.out.println(method);

//        BindDTO bindDTO = new BindDTO();
//        bindDTO.setUserQq("1853633282");
//        bindDTO.setUserId(1L);
//        userInfoService.updateUserBindByBindDTO(bindDTO);

//        System.out.println(userInfoService.countUniqueIdUsedByWrapper(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserEmail, "12@qq.com.cn")));

//        for(Annotation annotation :UserInfo.class.getDeclaredField("userSex").getAnnotations()){
//            System.out.println(annotation.annotationType().getName());
//        }

//        System.out.println(UserInfo.class.getDeclaredField("userEmail").getAnnotation(ConvertIgnore.class));



//        UserInfoDTOAndVO.TestDTO testDTO = new UserInfoDTOAndVO.TestDTO();
//        testDTO.setSex("F");
//        testDTO.setUserId(12313L);
//        UserInfo userInfo = ConvertDTOAndDO.ConvertDTOToDO(testDTO, UserInfo.class);
//        System.out.println(userInfo.getUserSex());
//        System.out.println(userInfo.getUserId());
//
//        testConvert(100, resetEmailDTO);
//        testConvert(1000, resetEmailDTO);
//        testConvert(10000, resetEmailDTO);
//        testConvert(100000, resetEmailDTO);
//        testConvert(1000000, resetEmailDTO);
//        testMapper(100, resetEmailDTO);
//        testMapper(1000, resetEmailDTO);
//        testMapper(10000, resetEmailDTO);
//        testMapper(100000, resetEmailDTO);
//        testMapper(1000000, resetEmailDTO);
//        testMapper(1000000000, resetEmailDTO);


//        GenerateIdUtil.toHex(16);

//        UserInfoDTO.RegisterDTO userBasicInfoDTO = new UserInfoDTO.RegisterDTO();
//        userBasicInfoDTO.setUserEmail("1853633282@qq.com");
//        userBasicInfoDTO.setUserGithub("http://www.baidu.com");
//        userBasicInfoDTO.setUserName("fallen-angle");
//        userBasicInfoDTO.setUserPassword("13291004986");
//        userBasicInfoDTO.setUserRoleId(1L);
//        userInfoService.insertUserByDTO(userBasicInfoDTO);
//        System.out.println(userInfoService.selectUserBasicInfoByUsername("fallen-
//        System.out.println(userInfoService.selectUserPasswordById(52721L));
//        System.out.println(new BCryptPasswordEncoder().matches("13291004986", userInfoService.selectUserPasswordById(52721L)));
//        System.out.println(userInfoService.selectUserBasicInfoByUsername("fallen-angle"));
    }

//    private void testMapper(int times, UserInfoDTOAndVO.ResetEmailDTO resetEmailDTO){
//        StopWatch stopwatch = new StopWatch();
//        stopwatch.start();
//
//        for (int i = 0; i < times; i++) {
//            UserInfo userInfo = new UserInfo();
//            UserInfoConverter.INSTANCE.resetEmailDTO2UserInfoDO(resetEmailDTO);
//        }
//
//        stopwatch.stop();
//        System.out.println("Mapping cost :" + stopwatch.getTotalTimeMillis());
//    }
//
//    private void testConvert(int times, UserInfoDTOAndVO.ResetEmailDTO resetEmailDTO){
//        StopWatch stopwatch = new StopWatch();
//        stopwatch.start();
//
//        for (int i = 0; i < times; i++) {
//            UserInfo userInfo = ConvertDTOAndDO.ConvertDTOToDO(resetEmailDTO, UserInfo.class);
//        }
//
//        stopwatch.stop();
//        System.out.println("Convert cost :" + stopwatch.getTotalTimeMillis());
//    }


}
