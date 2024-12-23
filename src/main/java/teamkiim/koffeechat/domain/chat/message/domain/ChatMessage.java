package teamkiim.koffeechat.domain.chat.message.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private String id;                      // mongoDB의 고유 식별자 ObjectId 사용

    @Indexed
    private Long seqId;                       // 순차 Id

    @Indexed
    private Long chatRoomId;                // 채팅방 id 인덱싱

    private String content;                 // 채팅 메세지

    private Long senderId;                  // 채팅을 작성한 회원

    private MessageType messageType;        // 메세지 타입 (입장, 채팅, 이미지)

    private LocalDateTime createdTime;        // 작성 시간

    @Builder
    public ChatMessage(Long seqId, Long chatRoomId, String content, Long senderId, MessageType messageType,
                       LocalDateTime createdTime) {
        this.seqId = seqId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.senderId = senderId;
        this.messageType = messageType;
        this.createdTime = createdTime;
    }
}
