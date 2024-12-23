package teamkiim.koffeechat.domain.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teamkiim.koffeechat.domain.chat.message.dto.request.ChatMessageServiceRequest;
import teamkiim.koffeechat.domain.notification.dto.request.CreateChatNotificationRequest;
import teamkiim.koffeechat.domain.notification.repository.EmitterRepository;
import teamkiim.koffeechat.domain.notification.service.emitter.SseEmitterWrapper;
import teamkiim.koffeechat.global.aescipher.AESCipherUtil;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatNotificationService {

    private final EmitterRepository emitterRepository;

    private final AESCipherUtil aesCipherUtil;

    /**
     * 알림 발송
     *
     * @param response 알림 내용
     */
    private void sendNotification(String emitterId, SseEmitter emitter, String eventId, Object response) {
        try {
            emitter.send(SseEmitter.event().id(eventId).name("chat").data(response));
        } catch (IOException e) {
            log.error("Failed to send notification, eventId={}, emitterId={}, error={}", eventId, emitterId,
                    e.getMessage());
            emitter.completeWithError(e);
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 채팅 알림 생성 : 채팅방 sse 알림 수신 설정이 되어있으면 알림을 보낸다.
     */
    public void createChatNotification(ChatMessageServiceRequest chatMessageServiceRequest,
                                       Long chatRoomId, Long senderId, List<Long> receiverIds) {

        for (Long receiverId : receiverIds) {
            String eventId = aesCipherUtil.encrypt(receiverId) + "_" + System.currentTimeMillis();   //eventId 생성

            Map<String, SseEmitterWrapper> emitters = emitterRepository.findReceiveEmitterByReceiverId(
                    aesCipherUtil.encrypt(receiverId));  //알림 수신 설정 되어있는 emitter 확인

            CreateChatNotificationRequest createChatNotificationRequest = CreateChatNotificationRequest.builder()
                    .chatRoomId(aesCipherUtil.encrypt(chatRoomId))
                    .content(chatMessageServiceRequest.getContent())
                    .senderId(aesCipherUtil.encrypt(senderId))
                    .messageType(chatMessageServiceRequest.getMessageType())
                    .createdTime(chatMessageServiceRequest.getCreatedTime())
                    .build();

            emitters.forEach((id, emitterWrapper) -> {
                if (emitterWrapper.isSseAlertActive(chatRoomId)) {  // 해당 채팅방에 대해 알림 수신 설정이 되어있는 사용자에게 알림 발송
                    sendNotification(id, emitterWrapper.getSseEmitter(), eventId, createChatNotificationRequest);
                }
            });
        }
    }

    /**
     * 채팅방 접속/미접속 시 sse 알림 상태 on/off
     */
    public void onChatRoomNotification(Long receiverId, Long chatRoomId) {  // 채팅방 미접속시 : 알림 on
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));

        emitters.forEach((id, emitter) -> {
            emitter.onChatRoomNotificationStatus(chatRoomId);
        });
    }

    public void offChatRoomNotification(Long receiverId, Long chatRoomId) {   // 채팅방 접속시 : 알림 off
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));

        emitters.forEach((id, emitter) -> {
            emitter.offChatRoomNotificationStatus(chatRoomId);
        });
    }

    /**
     * 채팅방 입장/퇴장 시 알림 설정 추가/삭제
     */
    public void addChatRoomNotification(Long receiverId, Long chatRoomId) {
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));

        emitters.forEach((id, emitter) -> {
            emitter.addChatRoomNotificationStatus(chatRoomId);
        });
    }

    public void removeChatRoomNotification(Long receiverId, Long chatRoomId) {
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));

        emitters.forEach((id, emitter) -> {
            emitter.removeChatRoomNotificationStatus(chatRoomId);
        });
    }

    /**
     * 채팅 접속/미접속 시 알림 on/off
     */
    public void startNotifications(Long receiverId) {
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));  //알림 받는 사람이 연결되어있는 모든 emitter에 이벤트 발송

        emitters.forEach((id, emitter) -> {
            emitter.startReceiving();
        });
    }

    public void stopNotifications(Long receiverId) {
        Map<String, SseEmitterWrapper> emitters = emitterRepository.findAllEmitterByReceiverId(
                aesCipherUtil.encrypt(receiverId));  //알림 받는 사람이 연결되어있는 모든 emitter에 이벤트 발송

        emitters.forEach((id, emitter) -> {
            emitter.stopReceiving();
        });
    }
}