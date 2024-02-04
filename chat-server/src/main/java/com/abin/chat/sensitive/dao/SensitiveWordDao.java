package com.abin.chat.sensitive.dao;

import com.abin.chat.sensitive.mapper.SensitiveWordMapper;
import com.abin.chat.sensitive.domain.SensitiveWord;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 敏感词DAO
 *
 * @author zhaoyuhang
 * @since 2023/06/11
 */
@Service
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {

}
