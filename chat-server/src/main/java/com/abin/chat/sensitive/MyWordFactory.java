package com.abin.chat.sensitive;

import com.abin.chat.sensitive.dao.SensitiveWordDao;
import com.abin.chat.sensitive.domain.SensitiveWord;
import com.abin.chat.common.algorithm.sensitiveWord.IWordFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyWordFactory implements IWordFactory {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}
