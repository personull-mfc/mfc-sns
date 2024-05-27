package com.mfc.sns.posting.application;

import com.mfc.sns.posting.dto.req.DeletePostReqDto;
import com.mfc.sns.posting.dto.req.UpdatePostReqDto;
import com.mfc.sns.posting.dto.resp.PostDetailRespDto;
import com.mfc.sns.posting.dto.resp.PostListRespDto;

public interface PostService {
	void createPost(String uuid, UpdatePostReqDto dto);
	PostDetailRespDto getPostDetail(Long postId);
	PostListRespDto getPostList(String partnerId);
	void deletePosts(String uuid, DeletePostReqDto dto);
	void updatePost(Long postId, String partnerId, UpdatePostReqDto dto);
}
