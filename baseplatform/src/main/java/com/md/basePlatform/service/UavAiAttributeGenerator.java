package com.md.basePlatform.service;

import com.md.basePlatform.domain.Uav;

/**
 * 无人机扩展属性生成（占位实现，可替换为真实 AI 服务）。
 */
public interface UavAiAttributeGenerator {

    /**
     * 在新增时根据已有字段补全可选属性。
     *
     * @param draft 即将持久化的实体（部分字段可能为空）
     */
    void enrichOnCreate(Uav draft);
}
