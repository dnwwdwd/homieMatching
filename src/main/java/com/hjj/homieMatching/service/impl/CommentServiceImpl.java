package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.manager.RedisBloomFilter;
import com.hjj.homieMatching.mapper.CommentMapper;
import com.hjj.homieMatching.model.domain.Blog;
import com.hjj.homieMatching.model.domain.Comment;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.CommentAddRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.CommentVO;
import com.hjj.homieMatching.service.BlogService;
import com.hjj.homieMatching.service.CommentService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 何佳骏
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2024-08-05 21:09:55
*/
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Resource
    private UserService userService;

    @Resource
    private RedisBloomFilter redisBloomFilter;

    @Resource
    @Lazy
    private BlogService blogService;

    @Override
    public boolean addComment(CommentAddRequest commentAddRequest, HttpServletRequest request) {
        String text = commentAddRequest.getText();
        Long blogId = commentAddRequest.getBlogId();
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (StringUtils.isEmpty(text) || text.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容长于512");
        }
        if (!redisBloomFilter.blogIsContained(blogId)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "博客不存在");
        }
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUserId(userId);
        comment.setBlogId(blogId);
        boolean save = this.save(comment);
        // 更新评论数
        UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
        // 博客评论数 + 1
        updateWrapper.eq("id", blogId);
        updateWrapper.setSql("commentNum = commentNum + 1");
        boolean update = blogService.update(updateWrapper);
        if (!update) {
            log.error("增加博客：{} 评论数失败", blogId);
        }
        return save;
    }

    @Override
    public boolean deleteComment(DeleteRequest deleteRequest, HttpServletRequest request) {
        long id = deleteRequest.getId();
        Comment comment = this.getById(id);
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        if (comment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "评论不存在");
        }
        if (!userService.isAdmin(request) && userId != comment.getUserId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        boolean b = this.removeById(id);
        return b;
    }

    @Override
    public List<CommentVO> listComments(long blogId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        List<Comment> commentList = this.lambdaQuery().eq(Comment::getBlogId, blogId).list();
        List<CommentVO> commentVOList = commentList.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            User user = userService.getById(comment.getUserId());
            commentVO.setUsername(user.getUsername());
            commentVO.setUserAvatarUrl(user.getAvatarUrl());
            commentVO.setIsMyComment(comment.getUserId() == userId);
            return commentVO;
        }).collect(Collectors.toList());
        return commentVOList;
    }

    @Override
    public boolean isMyComment(long userId, long commentId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "评论不存在");
        }
        return userId == comment.getUserId();
    }

}




