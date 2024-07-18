package com.hjj.homieMatching.controller;

import com.hjj.homieMatching.common.BaseResponse;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.common.ResultUtils;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.manager.AliOSSManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Resource
    private AliOSSManager aliOSSManager;

    @PostMapping("/blog/coverImage/upload")
    public BaseResponse<String> uploadBlogCoverImage(@RequestPart MultipartFile file) {
        String url = null;
        try {
            url = aliOSSManager.upload(file);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传图片失败");
        }
        return ResultUtils.success(url);
    }
}
