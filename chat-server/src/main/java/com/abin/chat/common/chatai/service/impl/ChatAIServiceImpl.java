package com.abin.chat.common.chatai.service.impl;

import com.abin.chat.common.chat.domain.entity.Message;
import com.abin.chat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.chat.common.chatai.handler.AbstractChatAIHandler;
import com.abin.chat.common.chatai.handler.ChatAIHandlerFactory;
import com.abin.chat.common.chatai.service.IChatAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatAIServiceImpl implements IChatAIService {
    @Override
    public void chat(Message message) {
        MessageExtra extra = message.getExtra();
        if (extra == null) {
            return;
        }
//        AbstractChatAIHandler chatAI = ChatAIHandlerFactory.getChatAIHandlerByName(message.getContent());
        AbstractChatAIHandler chatAI = ChatAIHandlerFactory.getChatAIHandlerById(extra.getAtUidList());
        if (chatAI != null) {
            chatAI.chat(message);
        }
    }
}