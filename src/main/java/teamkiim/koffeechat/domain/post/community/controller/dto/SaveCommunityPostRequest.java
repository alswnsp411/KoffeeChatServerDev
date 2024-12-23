package teamkiim.koffeechat.domain.post.community.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.post.community.dto.request.SaveCommunityPostServiceRequest;
import teamkiim.koffeechat.domain.vote.dto.request.SaveVoteServiceRequest;

/**
 * 클라이언트로부터 받아오는 커뮤니티 게시물 content
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveCommunityPostRequest {

    private SaveCommunityPostInfoRequest saveCommunityPostInfoRequest;  //게시물 글 내용 dto
    private SaveVoteRequest saveVoteRequest;                            //게시물 투표 내용 dto

    public SaveCommunityPostServiceRequest toPostServiceRequest() {
        return SaveCommunityPostServiceRequest.builder()
                .title(this.saveCommunityPostInfoRequest.getTitle())
                .bodyContent(this.saveCommunityPostInfoRequest.getBodyContent())
                .fileUrlList(this.saveCommunityPostInfoRequest.getFileUrlList())
                .tagList(this.saveCommunityPostInfoRequest.getTagContentList())
                .build();
    }

    public SaveVoteServiceRequest toVoteServiceRequest() {
        return SaveVoteServiceRequest.builder()
                .title(this.saveVoteRequest.getTitle())
                .items(this.saveVoteRequest.getItems())
                .build();
    }
}
