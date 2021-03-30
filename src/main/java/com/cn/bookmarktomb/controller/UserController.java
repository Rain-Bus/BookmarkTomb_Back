package com.cn.bookmarktomb.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.IdUtil;
import com.cn.bookmarktomb.excepotion.BadRequestException;
import com.cn.bookmarktomb.model.cache.UserInfoCache;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.CodeInfoDTO.*;
import com.cn.bookmarktomb.model.dto.OnlineInfoDTO.*;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import com.cn.bookmarktomb.model.vo.UserInfoVO.*;
import com.cn.bookmarktomb.security.bean.JwtLoginProperties;
import com.cn.bookmarktomb.security.bean.JwtSecurityProperties;
import com.cn.bookmarktomb.security.token.JwtUser;
import com.cn.bookmarktomb.security.token.TokenProvider;
import com.cn.bookmarktomb.service.OnlineService;
import com.cn.bookmarktomb.service.UserInfoService;
import com.cn.bookmarktomb.service.VerifyCodeService;
import com.cn.bookmarktomb.util.CommonUtil;
import com.cn.bookmarktomb.util.MailUtil;
import com.cn.bookmarktomb.util.MongoUtil;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author fallen-angle
 * @since 2020-07-20
 * @date 2020-08-26 15:01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ApiOperation("Some operation about user info;")
public class UserController {

    private final MailUtil mailUtil;
    private final OnlineService onlineService;
    private final TokenProvider tokenProvider;
    private final UserInfoService userInfoService;
    private final JwtLoginProperties loginProperties;
    private final UserInfoConverter userInfoConverter;
    private final VerifyCodeService verifyCodeService;
    private final JwtSecurityProperties securityProperties;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static final String USERNAME_FILED_NAME = "UName";
    private static final String USER_EMAIL_FIELD_NAME = "UEml";

    @PostMapping("/user")
    @ApiOperation("Register an account")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterVO registerVO) {
        MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USERNAME_FILED_NAME, registerVO.getUsername());
        MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USER_EMAIL_FIELD_NAME, registerVO.getEmail());
        verifyCodeService.selectResultById(registerVO.getEmail(), registerVO.getCode());
        RegisterDTO registerDTO = userInfoConverter.registerVO2DTO(registerVO);
        userInfoService.insertUser(registerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/login")
    @ApiOperation("User login")
    public ResponseEntity<Object> getUserInfo(@Valid @RequestBody LoginVO loginVO, HttpServletRequest request) {
        String password = loginVO.getPassword();
        verifyCodeService.selectResultById(loginVO.getCodeUid(), loginVO.getCode().toLowerCase());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVO.getAccount(), password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtUser jwtUser = (JwtUser)authentication.getPrincipal();
        String token = tokenProvider.generateToken(jwtUser.getUserBasicInfoVO());
        onlineService.insertUser(getInsertOnlineInfoDTO(jwtUser, token, loginVO.getRememberMe(), request));
        Map<String, Object> authInfo =MapBuilder.<String, Object>create()
                .put("token", securityProperties.getTokenPrefix() + token)
                .map();
        return ResponseEntity.ok(authInfo);
    }

    @PutMapping("/user")
    @ApiOperation("Reset user info")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> setUserInfo(@ApiParam(hidden = true) Long userId,
                                      @Valid @RequestBody ResetOtherInfoVO resetOtherInfoVO) {
        resetOtherInfoVO.setId(userId);
        ResetOtherInfoDTO resetOtherInfoDTO = userInfoConverter.resetOtherInfoVO2DTO(resetOtherInfoVO);
        userInfoService.updateOtherInfo(resetOtherInfoDTO);
        UserInfoCache.removeUserFromCacheByUserId(resetOtherInfoDTO.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/email")
    @ApiOperation(value = "Reset user email")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> resetEmail(@ApiParam(hidden = true) Long userId,
                                 @Valid @RequestBody ResetEmailVO resetEmailVO) {
        verifyCodeService.selectResultById(resetEmailVO.getEmail(), resetEmailVO.getCode());
        String encodedPassword = userInfoService.selectPasswordById(userId);
        if (!getEncoder().matches(resetEmailVO.getPassword(), encodedPassword)) {
            throw new BadRequestException(ErrorCodeConstant.USER_PWD_ERROR_CODE, "User password is error;");
        }
        MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USER_EMAIL_FIELD_NAME, resetEmailVO.getEmail());
        resetEmailVO.setId(userId);
        ResetEmailDTO resetEmailDTO = userInfoConverter.resetEmailVO2DTO(resetEmailVO);
        userInfoService.updateEmail(resetEmailDTO);
        onlineService.deleteById(resetEmailDTO.getId());
        UserInfoCache.removeUserFromCacheByUserId(resetEmailDTO.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/pwd")
    @ApiOperation("Reset user password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> resetPassword(@ApiParam(hidden = true) Long userId,
                                    @Valid @RequestBody ResetPasswordVO resetPasswordVO) {
        String encodedPassword = userInfoService.selectPasswordById(userId);
        if (!getEncoder().matches(resetPasswordVO.getOldPassword(), encodedPassword)) {
            throw new BadRequestException(ErrorCodeConstant.USER_PWD_ERROR_CODE, "Old password is error;");
        }
        resetPasswordVO.setId(userId);
        ResetPasswordDTO resetPasswordDTO = userInfoConverter.resetPasswordVO2DTO(resetPasswordVO);
        userInfoService.updatePassword(resetPasswordDTO);
        onlineService.deleteById(resetPasswordDTO.getId());
        UserInfoCache.removeUserFromCacheByUserId(resetPasswordDTO.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/avatar")
    @ApiOperation("Set user avatar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> resetAvatar(@ApiParam(hidden = true) Long userId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String avatarPath = System.getProperty("user.home") + "/.bookmark_tomb/avatar/" + userId + ".jpg";
        File avatarFile = new File(avatarPath);
        FileUtil.writeBytes(file.getBytes(), avatarFile);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/forget/password")
    @ApiOperation(value = "Reset password by email")
    public ResponseEntity<Object> forgetPassword(@Valid @RequestBody ForgetPasswordVO forgetPasswordVO) {
        verifyCodeService.selectResultById(forgetPasswordVO.getEmail(), forgetPasswordVO.getCode());
        ForgetPasswordDTO forgetPasswordDTO = userInfoConverter.forgetPasswordVO2DTO(forgetPasswordVO);
        userInfoService.updateForgetPassword(forgetPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user")
    @ApiOperation("Logoff not admin account")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Object> logoff(@ApiParam(hidden = true) Long userId){
        userInfoService.deleteById(userId);
        onlineService.deleteById(userId);
        UserInfoCache.removeUserFromCacheByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    @ApiOperation("Get user info")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> getUserInfo(@ApiParam(hidden = true) Long userId) {
        UserBasicInfoDTO userBasicInfoDTO = userInfoService.selectById(userId);
        return ResponseEntity.ok(userInfoConverter.userBasicInfoDTO2VO(userBasicInfoDTO));
    }

    @GetMapping("/user/avatar")
    @ApiOperation(value = "Get user avatar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<byte[]> getAvatar(Long userId) {
        String avatarPathStr = System.getProperty("user.home") + "/.bookmark_tomb/avatar/" + userId + ".jpg";
        File file = new File(avatarPathStr);
        if (file.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(FileUtil.readBytes(file), headers, HttpStatus.OK);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/logout")
    @ApiOperation(value = "Logout account")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> logout(String userToken){
        onlineService.deleteByToken(userToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/code/img")
    @ApiOperation(value = "Get image verify code")
    public ResponseEntity<Object> getVerifyCode() {

        Captcha captcha = loginProperties.getCaptcha();
        String uuid = IdUtil.simpleUUID();

        InsertCodeDTO insertCodeDTO = new InsertCodeDTO();
        insertCodeDTO.setResult(captcha.text());
        insertCodeDTO.setUid(uuid);
        verifyCodeService.insertImgCode(insertCodeDTO);

        Map<String, Object> result = MapBuilder.<String, Object>create()
                .put("codeImg", captcha.toBase64())
                .put("codeUid", uuid)
                .map();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/code/email/{email}")
    @ApiOperation(value = "Get email verify code")
    public ResponseEntity<Object> getEmailVerifyCode(@PathVariable("email") @Email String email) {
        InsertCodeDTO insertCodeDTO = new InsertCodeDTO();
        insertCodeDTO.setUid(email);
        insertCodeDTO.setResult(CommonUtil.getEmailVerifyCode());
        verifyCodeService.insertEmailCode(insertCodeDTO);
        mailUtil.sendVerifyCodeEmail(email, insertCodeDTO.getResult());
        return ResponseEntity.ok().build();
    }

    private InsertOnlineInfoDTO getInsertOnlineInfoDTO(JwtUser jwtUser, String token, Boolean rememberMe, HttpServletRequest request) {
        String ip = CommonUtil.getIp(request);
        String address = CommonUtil.getAddress(ip);
        String device = CommonUtil.getBrowser(request);
        InsertOnlineInfoDTO insertOnlineInfoDTO = new InsertOnlineInfoDTO();
        insertOnlineInfoDTO.setIp(ip);
        insertOnlineInfoDTO.setToken(token);
        insertOnlineInfoDTO.setDevice(device);
        insertOnlineInfoDTO.setAddress(address);
        insertOnlineInfoDTO.setRememberMe(rememberMe);
        insertOnlineInfoDTO.setId(jwtUser.getUserBasicInfoVO().getId());
        insertOnlineInfoDTO.setUsername(jwtUser.getUserBasicInfoVO().getUsername());
        return insertOnlineInfoDTO;
    }

    private BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}