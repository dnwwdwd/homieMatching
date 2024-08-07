package com.hjj.homieMatching.service;

import com.hjj.homieMatching.model.domain.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hjj.homieMatching.model.request.CommentAddRequest;
import com.hjj.homieMatching.model.request.DeleteRequest;
import com.hjj.homieMatching.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 何佳骏
* @description 针对表【comment(评论表)】的数据库操作Service
* @createDate 2024-08-05 21:09:55
*/
public interface CommentService extends IService<Comment> {

    boolean addComment(CommentAddRequest commentAddRequest, HttpServletRequest request);

    boolean deleteComment(DeleteRequest deleteRequest, HttpServletRequest request);

    List<CommentVO> listComments(long blogId, HttpServletRequest request);

    boolean isMyComment(long userId, long commentId);
}
