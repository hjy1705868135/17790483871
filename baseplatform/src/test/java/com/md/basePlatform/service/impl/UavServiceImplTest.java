package com.md.basePlatform.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.domain.UavForm;
import com.md.basePlatform.exception.DuplicateCodeException;
import com.md.basePlatform.exception.UavNotFoundException;
import com.md.basePlatform.repository.UavMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UavServiceImplTest {

    @Mock
    private UavMapper uavMapper;

    private UavServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UavServiceImpl(uavMapper, new StubUavAiAttributeGenerator());
    }

    @Test
    void should_return_page_when_search_called() {
        when(uavMapper.countSearch(null, null)).thenReturn(2L);
        List<Uav> rows = Collections.singletonList(sampleUav(1L));
        when(uavMapper.searchPage(null, null, 0, 10)).thenReturn(rows);

        PageResult<Uav> page = service.page(null, null, 1, 10);

        assertEquals(2L, page.getTotal());
        assertEquals(1, page.getRecords().size());
        assertEquals(10, page.getPageSize());
    }

    @Test
    void should_clamp_page_size_when_exceeds_max() {
        when(uavMapper.countSearch(any(), any())).thenReturn(0L);
        when(uavMapper.searchPage(any(), any(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        PageResult<Uav> page = service.page(null, null, 1, 500);

        verify(uavMapper).searchPage(any(), any(), eq(0), eq(100));
        assertEquals(100, page.getPageSize());
    }

    @Test
    void should_throw_when_get_missing_id() {
        when(uavMapper.findById(99L)).thenReturn(null);

        assertThrows(UavNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void should_create_when_code_unique() {
        UavForm form = new UavForm();
        form.setCode("UAV-001");
        form.setStatus("ACTIVE");

        when(uavMapper.countByCode("UAV-001")).thenReturn(0);
        doAnswer(invocation -> {
            Uav u = invocation.getArgument(0);
            u.setId(7L);
            return 1;
        }).when(uavMapper).insert(any(Uav.class));
        Uav afterInsert = sampleUav(7L);
        afterInsert.setCode("UAV-001");
        when(uavMapper.findById(7L)).thenReturn(afterInsert);

        Uav created = service.create(form);

        assertNotNull(created.getId());
        assertEquals(7L, created.getId());
        verify(uavMapper).insert(any(Uav.class));
    }

    @Test
    void should_throw_duplicate_when_code_exists_on_create() {
        UavForm form = new UavForm();
        form.setCode("UAV-001");
        form.setStatus("ACTIVE");

        when(uavMapper.countByCode("UAV-001")).thenReturn(1);

        assertThrows(DuplicateCodeException.class, () -> service.create(form));
        verify(uavMapper, never()).insert(any());
    }

    @Test
    void should_update_when_exists() {
        UavForm form = new UavForm();
        form.setId(1L);
        form.setCode("UAV-001");
        form.setModel("M1");
        form.setManufacturer("ACME");
        form.setStatus("ACTIVE");

        Uav existing = sampleUav(1L);
        Uav after = sampleUav(1L);
        after.setCode("UAV-001");
        after.setModel("M1");
        after.setManufacturer("ACME");
        after.setStatus("ACTIVE");
        after.setUpdatedAt(LocalDateTime.now());

        when(uavMapper.findById(1L)).thenReturn(existing, after);
        when(uavMapper.countByCodeExcludeId("UAV-001", 1L)).thenReturn(0);

        Uav updated = service.update(form);

        assertEquals("UAV-001", updated.getCode());
        verify(uavMapper).update(any(Uav.class));
    }

    @Test
    void should_delete_when_exists() {
        when(uavMapper.findById(1L)).thenReturn(sampleUav(1L));

        service.delete(1L);

        verify(uavMapper).deleteById(1L);
    }

    @Test
    void should_throw_when_delete_missing() {
        when(uavMapper.findById(2L)).thenReturn(null);

        assertThrows(UavNotFoundException.class, () -> service.delete(2L));
        verify(uavMapper, never()).deleteById(anyLong());
    }

    private static Uav sampleUav(Long id) {
        Uav u = new Uav();
        u.setId(id);
        u.setCode("C-" + id);
        u.setModel("M");
        u.setManufacturer("MF");
        u.setStatus("ACTIVE");
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        return u;
    }
}
