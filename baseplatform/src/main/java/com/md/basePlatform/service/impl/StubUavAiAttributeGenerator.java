package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.service.UavAiAttributeGenerator;
import org.springframework.stereotype.Component;

/**
 * 占位 AI 生成器：为空字段填入合理默认值。
 */
@Component
public class StubUavAiAttributeGenerator implements UavAiAttributeGenerator {

    @Override
    public void enrichOnCreate(Uav draft) {
        if (draft.getModel() == null || draft.getModel().trim().isEmpty()) {
            draft.setModel("GENERIC-MODEL");
        }
        if (draft.getManufacturer() == null || draft.getManufacturer().trim().isEmpty()) {
            draft.setManufacturer("UNKNOWN");
        }
        if (draft.getMaxFlightTimeMinutes() == null) {
            draft.setMaxFlightTimeMinutes(30);
        }
        if (draft.getMaxRangeKm() == null) {
            draft.setMaxRangeKm(5);
        }
        if (draft.getPayloadKg() == null) {
            draft.setPayloadKg(1);
        }
    }
}
