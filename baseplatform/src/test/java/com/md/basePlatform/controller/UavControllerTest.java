package com.md.basePlatform.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.exception.GlobalExceptionHandler;
import com.md.basePlatform.exception.UavNotFoundException;
import com.md.basePlatform.service.IUavService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UavController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UavControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUavService uavService;

    @Test
    void should_return_list_view_when_get_uav() throws Exception {
        PageResult<Uav> page = new PageResult<>();
        when(uavService.page(any(), any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/uav"))
                .andExpect(status().isOk())
                .andExpect(view().name("uav/list"));
    }

    @Test
    void should_return_add_form_when_get_add() throws Exception {
        mockMvc.perform(get("/uav/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("uav/form"));
    }

    @Test
    void should_redirect_after_create_post() throws Exception {
        Uav created = new Uav();
        created.setId(1L);
        created.setCode("U-1");
        when(uavService.create(any())).thenReturn(created);

        mockMvc.perform(post("/uav")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", "U-1")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uav"));
    }

    @Test
    void should_return_detail_when_exists() throws Exception {
        Uav u = new Uav();
        u.setId(5L);
        u.setCode("X");
        u.setModel("M");
        u.setManufacturer("F");
        u.setStatus("ACTIVE");
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        when(uavService.getById(5L)).thenReturn(u);

        mockMvc.perform(get("/uav/detail/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("uav/detail"));
    }

    @Test
    void should_return_error_when_detail_missing() throws Exception {
        when(uavService.getById(9L)).thenThrow(new UavNotFoundException("not found"));

        mockMvc.perform(get("/uav/detail/9"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"));
    }
}
