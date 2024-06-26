package com.mfc.sns.posting.application;

import static com.mfc.sns.common.response.BaseResponseStatus.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mfc.sns.common.exception.BaseException;
import com.mfc.sns.posting.domain.Bookmark;
import com.mfc.sns.posting.dto.kafka.PostSummaryDto;
import com.mfc.sns.posting.dto.resp.PostDto;
import com.mfc.sns.posting.dto.resp.PostListRespDto;
import com.mfc.sns.posting.infrastructure.BookmarkRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final KafkaProducer producer;

	@Override
	public void createBookmark(Long postId, String userId) {
		if(bookmarkRepository.existsByPostIdAndUserId(postId, userId)) {
			throw new BaseException(BOOKMARK_CONFLICT);
		}

		bookmarkRepository.save(Bookmark.builder()
				.userId(userId)
				.post(postId)
				.build());

		producer.createBookmark(PostSummaryDto.builder().postId(postId).build());
	}

	@Override
	public void deleteBookmark(Long postId, String userId) {
		int cnt = bookmarkRepository.deleteByPostId(postId, userId);
		if(cnt > 0) producer.deleteBookmark(PostSummaryDto.builder().postId(postId).build());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isBookmarked(Long postId, String userId) {
		return bookmarkRepository.findByPostIdAndUserId(postId, userId).isPresent();
	}

	@Override
	@Transactional(readOnly = true)
	public PostListRespDto getBookmarkList(String userId, Pageable page) {
		Slice<PostDto> posts = bookmarkRepository.getBookmarkedPostList(userId, page);

		return PostListRespDto.builder()
				.posts(posts.getContent())
				.isLast(posts.isLast())
				.build();
	}
}
