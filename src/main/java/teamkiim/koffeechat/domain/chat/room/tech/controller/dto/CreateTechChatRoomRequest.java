package teamkiim.koffeechat.domain.chat.room.tech.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.chat.room.tech.dto.request.CreateTechChatRoomServiceRequest;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;
import teamkiim.koffeechat.domain.post.dev.domain.ParentSkillCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTechChatRoomRequest {

    @NotBlank(message = "채팅방 이름은 필수 입력 입니다.")
    private String name;
    @NotNull(message = "상위 카테고리 선택은 필수입니다.")
    private ParentSkillCategory parentSkillCategory;
    @NotNull(message = "하위 카테고리 선택은 필수입니다.")
    private ChildSkillCategory childSkillCategory;

    public CreateTechChatRoomServiceRequest toServiceRequest(){
        return CreateTechChatRoomServiceRequest.builder()
                .name(name)
                .parentSkillCategory(parentSkillCategory)
                .childSkillCategory(childSkillCategory)
                .build();
    }
}