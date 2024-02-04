package com.abin.chat.common.chatai.service;

import com.abin.chat.common.chat.domain.entity.Message;

public interface IChatAIService {

    void chat(Message message);
}
