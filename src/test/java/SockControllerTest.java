import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.sock.SockServer;
import ru.sock.controller.SockController;
import ru.sock.dto.SockDto;
import ru.sock.service.SockService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SockController.class)
@ContextConfiguration(classes = SockServer.class)
public class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @Test
    void registerIncome_ShouldReturnStatusOk() throws Exception {
        SockDto sockDto = new SockDto("red", 80, 100);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sockDto)))
                .andExpect(status().isOk());

        verify(sockService, times(1)).registerIncome(any(SockDto.class));
    }

    @Test
    void registerOutcome_ShouldReturnStatusOk() throws Exception {
        SockDto sockDto = new SockDto("blue", 50, 30);

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sockDto)))
                .andExpect(status().isOk());

        verify(sockService, times(1)).registerOutcome(any(SockDto.class));
    }

    @Test
    void filterSocks_ShouldReturnSockList() throws Exception {
        List<SockDto> sockDtos = List.of(new SockDto("green", 60, 10));
        when(sockService.filterSocks("green", "equal", 60)).thenReturn(sockDtos);

        mockMvc.perform(get("/api/socks")
                        .param("color", "green")
                        .param("operator", "equal")
                        .param("cottonPercentage", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("green"))
                .andExpect(jsonPath("$[0].cottonPercentage").value(60))
                .andExpect(jsonPath("$[0].quantity").value(10));

        verify(sockService, times(1)).filterSocks("green", "equal", 60);
    }

    @Test
    void batchUpload_ShouldReturnStatusOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "color,cottonPercentage,quantity\nred,80,50".getBytes()
        );

        mockMvc.perform(multipart("/api/socks/batch").file(file))
                .andExpect(status().isOk());

        verify(sockService, times(1)).batchUploadFromCSV(anyString());
    }
}
