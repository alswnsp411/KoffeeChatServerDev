package teamkiim.koffeechat.domain.comment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teamkiim.koffeechat.domain.comment.controller.dto.request.CommentRequest;
import teamkiim.koffeechat.domain.comment.controller.dto.request.ModifyCommentRequest;
import teamkiim.koffeechat.domain.comment.service.CommentService;
import teamkiim.koffeechat.domain.post.common.dto.response.MyPostListResponse;
import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    private final AESCipherUtil aesCipherUtil;


    /**
     * 댓글 작성
     */
    @AuthenticatedMemberPrincipal
    @PostMapping("")
    @CommentApiDocument.SaveCommentApiDoc
    public ResponseEntity<?> saveComment(@Valid @RequestBody CommentRequest commentRequest,
                                         HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));
        Long decryptedPostId = aesCipherUtil.decrypt(commentRequest.getPostId());

        LocalDateTime currDateTime = LocalDateTime.now();

        commentService.saveComment(commentRequest.toServiceRequest(decryptedPostId, currDateTime), memberId);

        return ResponseEntity.ok("댓글 저장 완료");
    }

    /**
     * 댓글 수정
     */
    @AuthenticatedMemberPrincipal
    @PatchMapping("")
    @CommentApiDocument.ModifyCommentApiDoc
    public ResponseEntity<?> modifyComment(@Valid @RequestBody ModifyCommentRequest modifyCommentRequest) {

        Long decryptedCommentId = aesCipherUtil.decrypt(modifyCommentRequest.getId());

        commentService.modifyComment(modifyCommentRequest.toServiceRequest(decryptedCommentId));

        return ResponseEntity.ok("댓글 수정 완료");
    }

    /**
     * 댓글 삭제
     */
    @AuthenticatedMemberPrincipal
    @DeleteMapping("/{commentId}")
    @CommentApiDocument.DeleteCommentApiDoc
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") String commentId) {

        commentService.deleteComment(aesCipherUtil.decrypt(commentId));

        return ResponseEntity.ok("댓글 삭제 완료");
    }

    /**
     * 마이페이지 내가 쓴 댓글 리스트 확인
     */
    @AuthenticatedMemberPrincipal
    @GetMapping("")
    @CommentApiDocument.MyCommentListApiDoc
    public ResponseEntity<?> findMyCommentList(@RequestParam("page") int page, @RequestParam("size") int size,
                                               HttpServletRequest request) {

        Long memberId = Long.valueOf(String.valueOf(request.getAttribute("authenticatedMemberPK")));

        List<MyPostListResponse> myPostListResponseList = commentService.findMyCommentList(memberId, page, size);

        return ResponseEntity.ok(myPostListResponseList);
    }
}
