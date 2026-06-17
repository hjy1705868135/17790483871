package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.domain.UavForm;
import com.md.basePlatform.exception.DuplicateCodeException;
import com.md.basePlatform.exception.UavNotFoundException;
import com.md.basePlatform.repository.UavMapper;
import com.md.basePlatform.service.IUavService;
import com.md.basePlatform.service.UavAiAttributeGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 无人机业务实现。
 */
@Service
public class UavServiceImpl implements IUavService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final UavMapper uavMapper;
    private final UavAiAttributeGenerator aiAttributeGenerator;

    /**
     * @param uavMapper 数据访问
     * @param aiAttributeGenerator AI 占位生成器
     */
    public UavServiceImpl(UavMapper uavMapper, UavAiAttributeGenerator aiAttributeGenerator) {
        this.uavMapper = uavMapper;
        this.aiAttributeGenerator = aiAttributeGenerator;
    }

    @Override
    public PageResult<Uav> page(String keyword, String status, int pageNum, int pageSize) {
        String kw = normalize(keyword);
        String st = normalize(status);
        int pn = Math.max(1, pageNum);
        int ps = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        long total = uavMapper.countSearch(kw, st);
        int offset = (pn - 1) * ps;
        PageResult<Uav> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pn);
        result.setPageSize(ps);
        result.setRecords(uavMapper.searchPage(kw, st, offset, ps));
        return result;
    }

    @Override
    public Uav getById(long id) {
        Uav uav = uavMapper.findById(id);
        if (uav == null) {
            throw new UavNotFoundException("无人机不存在: id=" + id);
        }
        return uav;
    }

    @Override
    @Transactional
    public Uav create(UavForm form) {
        Uav uav = fromForm(form);
        uav.setId(null);
        LocalDateTime now = LocalDateTime.now();
        uav.setCreatedAt(now);
        uav.setUpdatedAt(now);
        aiAttributeGenerator.enrichOnCreate(uav);
        if (uavMapper.countByCode(uav.getCode()) > 0) {
            throw new DuplicateCodeException("编号已存在: " + uav.getCode());
        }
        uavMapper.insert(uav);
        return uavMapper.findById(uav.getId());
    }

    @Override
    @Transactional
    public Uav update(UavForm form) {
        if (form.getId() == null) {
            throw new IllegalArgumentException("更新时 id 不能为空");
        }
        Uav existing = uavMapper.findById(form.getId());
        if (existing == null) {
            throw new UavNotFoundException("无人机不存在: id=" + form.getId());
        }
        if (uavMapper.countByCodeExcludeId(form.getCode(), form.getId()) > 0) {
            throw new DuplicateCodeException("编号已存在: " + form.getCode());
        }
        Uav uav = fromForm(form);
        if (uav.getModel() == null) {
            uav.setModel(existing.getModel());
        }
        if (uav.getManufacturer() == null) {
            uav.setManufacturer(existing.getManufacturer());
        }
        if (uav.getMaxAltitudeMeters() == null) {
            uav.setMaxAltitudeMeters(existing.getMaxAltitudeMeters());
        }
        if (uav.getMaxSpeedKmh() == null) {
            uav.setMaxSpeedKmh(existing.getMaxSpeedKmh());
        }
        if (uav.getBatteryCapacityMah() == null) {
            uav.setBatteryCapacityMah(existing.getBatteryCapacityMah());
        }
        if (uav.getWeightKg() == null) {
            uav.setWeightKg(existing.getWeightKg());
        }
        if (uav.getCameraResolution() == null) {
            uav.setCameraResolution(existing.getCameraResolution());
        }
        if (uav.getPurchaseDate() == null) {
            uav.setPurchaseDate(existing.getPurchaseDate());
        }
        if (uav.getWarrantyExpireDate() == null) {
            uav.setWarrantyExpireDate(existing.getWarrantyExpireDate());
        }
        if (uav.getSerialNumber() == null) {
            uav.setSerialNumber(existing.getSerialNumber());
        }
        if (uav.getDepartment() == null) {
            uav.setDepartment(existing.getDepartment());
        }
        if (uav.getApplicationField() == null) {
            uav.setApplicationField(existing.getApplicationField());
        }
        uav.setCreatedAt(existing.getCreatedAt());
        uav.setUpdatedAt(LocalDateTime.now());
        uavMapper.update(uav);
        return uavMapper.findById(uav.getId());
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (uavMapper.findById(id) == null) {
            throw new UavNotFoundException("无人机不存在: id=" + id);
        }
        uavMapper.deleteById(id);
    }

    private static Uav fromForm(UavForm form) {
        Uav u = new Uav();
        u.setId(form.getId());
        u.setCode(form.getCode().trim());
        u.setModel(trimToNull(form.getModel()));
        u.setManufacturer(trimToNull(form.getManufacturer()));
        u.setMaxFlightTimeMinutes(form.getMaxFlightTimeMinutes());
        u.setMaxRangeKm(form.getMaxRangeKm());
        u.setPayloadKg(form.getPayloadKg());
        u.setMaxAltitudeMeters(form.getMaxAltitudeMeters());
        u.setMaxSpeedKmh(form.getMaxSpeedKmh());
        u.setBatteryCapacityMah(form.getBatteryCapacityMah());
        u.setWeightKg(form.getWeightKg());
        u.setCameraResolution(trimToNull(form.getCameraResolution()));
        u.setHasGps(form.getHasGps());
        u.setHasObstacleAvoidance(form.getHasObstacleAvoidance());
        u.setPurchaseDate(parseDate(form.getPurchaseDate()));
        u.setWarrantyExpireDate(parseDate(form.getWarrantyExpireDate()));
        u.setSerialNumber(trimToNull(form.getSerialNumber()));
        u.setDepartment(trimToNull(form.getDepartment()));
        u.setApplicationField(trimToNull(form.getApplicationField()));
        u.setStatus(form.getStatus());
        u.setRemark(trimToNull(form.getRemark()));
        return u;
    }

    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
