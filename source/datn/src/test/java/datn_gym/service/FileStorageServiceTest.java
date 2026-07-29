package datn_gym.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storesValidImageUsingGeneratedSafeName() throws Exception {
        FileStorageService service = new FileStorageService(tempDir);
        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "../../avatar.png",
                "image/png",
                validPng());

        String url = service.storeFile(upload);

        assertThat(url).startsWith("/api/files/download/").endsWith(".png");
        String storedName = url.substring(url.lastIndexOf('/') + 1);
        assertThat(tempDir.resolve(storedName)).isRegularFile();
    }

    @Test
    void rejectsFakeImageEvenWhenMimeTypeLooksValid() {
        FileStorageService service = new FileStorageService(tempDir);
        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "fake.png",
                "image/png",
                "not an image".getBytes());

        assertThatThrownBy(() -> service.storeFile(upload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không phải là ảnh");
    }

    @Test
    void blocksPathTraversalWhenLoading() {
        FileStorageService service = new FileStorageService(tempDir);

        assertThatThrownBy(() -> service.loadFileAsResource("../secret.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Đường dẫn");
    }

    private byte[] validPng() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }
}
