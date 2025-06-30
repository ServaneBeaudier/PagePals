package com.pagepals.circle.service;

import java.util.List;

import com.pagepals.circle.dto.MessageDTO;

public interface MessageService {

    void sendMessage(MessageDTO dto);
    List<MessageDTO> getMessagesByCircleId(long circleId);
}
