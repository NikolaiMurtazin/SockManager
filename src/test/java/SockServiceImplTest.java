import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sock.dto.SockDto;
import ru.sock.mapper.SockMapper;
import ru.sock.model.Sock;
import ru.sock.repository.SockRepository;
import ru.sock.service.SockServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SockServiceImplTest {

    @Mock
    private SockRepository sockRepository;

    @Mock
    private SockMapper sockMapper;

    @InjectMocks
    private SockServiceImpl sockService;

    @Test
    void testRegisterIncome_NewSock() {
        // Arrange
        SockDto sockDto = new SockDto("red", 50, 10);
        Sock savedSock = new Sock(1L,"red", 50, 10);
        Mockito.when(sockRepository.findByColorAndCottonPercentage("red", 50)).thenReturn(List.of());
        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenReturn(savedSock);

        // Act
        sockService.registerIncome(sockDto);

        // Assert
        Mockito.verify(sockRepository, Mockito.times(1)).save(Mockito.any(Sock.class));
    }

    @Test
    void testRegisterIncome_ExistingSock() {
        // Arrange
        SockDto sockDto = new SockDto("blue", 40, 20);
        Sock existingSock = new Sock(1L, "blue", 40, 10);
        Sock updatedSock = new Sock(1L,"blue", 40, 30);
        Mockito.when(sockRepository.findByColorAndCottonPercentage("blue", 40)).thenReturn(List.of(existingSock));
        Mockito.when(sockRepository.save(existingSock)).thenReturn(updatedSock);

        // Act
        sockService.registerIncome(sockDto);

        // Assert
        Assertions.assertEquals(30, existingSock.getQuantity());
        Mockito.verify(sockRepository).save(existingSock);
    }

    @Test
    void testRegisterOutcome_SufficientQuantity() {
        // Arrange
        SockDto sockDto = new SockDto("green", 30, 5);
        Sock existingSock = new Sock(1L,"green", 30, 10);
        Mockito.when(sockRepository.findByColorAndCottonPercentage("green", 30)).thenReturn(List.of(existingSock));

        // Act
        sockService.registerOutcome(sockDto);

        // Assert
        Assertions.assertEquals(5, existingSock.getQuantity());
        Mockito.verify(sockRepository).save(existingSock);
    }

    @Test
    void testRegisterOutcome_InsufficientQuantity() {
        // Arrange
        SockDto sockDto = new SockDto("green", 30, 15);
        Sock existingSock = new Sock(1L,"green", 30, 10);
        Mockito.when(sockRepository.findByColorAndCottonPercentage("green", 30)).thenReturn(List.of(existingSock));

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> sockService.registerOutcome(sockDto));
        Mockito.verify(sockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testFilterSocks() {
        // Arrange
        Sock sock1 = new Sock(1L,"yellow", 70, 15);
        Sock sock2 = new Sock(2L,"yellow", 80, 20);
        Mockito.when(sockRepository.findByColorAndCottonPercentageGreaterThan("yellow", 60))
                .thenReturn(List.of(sock1, sock2));

        // Act
        List<SockDto> result = sockService.filterSocks("yellow", "moreThan", 60);

        // Assert
        Assertions.assertEquals(2, result.size());
        Mockito.verify(sockRepository).findByColorAndCottonPercentageGreaterThan("yellow", 60);
    }

    @Test
    void testBatchUploadFromCSV_ValidData() throws IOException {
        // Arrange
        Path filePath = Paths.get("src/test/resources/socks.csv");
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "red,50,10\nblue,40,20\n".getBytes());
        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sockService.batchUploadFromCSV(filePath.toString());

        // Assert
        Mockito.verify(sockRepository, Mockito.times(2)).save(Mockito.any(Sock.class));

        // Cleanup
        Files.delete(filePath);
    }

    @Test
    void testBatchUploadFromCSV_InvalidData() throws IOException {
        // Arrange
        Path filePath = Paths.get("src/test/resources/socks_invalid.csv");
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "red,-10,10\nblue,40,-20\n".getBytes());

        // Act & Assert
        Assertions.assertThrows(RuntimeException.class, () -> sockService.batchUploadFromCSV(filePath.toString()));

        // Cleanup
        Files.delete(filePath);
    }
}
