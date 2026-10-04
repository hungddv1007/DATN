package datn_gym.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_IMAGE_PIXELS = 25_000_000L;
    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif"
    );

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this(Paths.get(uploadDir));
    }

    FileStorageService(Path uploadDir) {
        this.fileStorageLocation = uploadDir.toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể khởi tạo thư mục lưu trữ.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn một ảnh để tải lên.");
        }

        String extension = ALLOWED_IMAGE_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Chỉ hỗ trợ ảnh JPG, PNG hoặc GIF.");
        }

        validateImageContent(file);

        String fileName = UUID.randomUUID() + extension;
        Path targetLocation = fileStorageLocation.resolve(fileName).normalize();
        ensureInsideStorage(targetLocation);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // URL tương đối hoạt động đúng qua reverse proxy và ở mọi môi trường.
            return "/api/files/download/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể lưu ảnh. Vui lòng thử lại.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh.");
        }

        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        ensureInsideStorage(filePath);

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable() && Files.isRegularFile(filePath)) {
                return resource;
            }
        } catch (MalformedURLException ignored) {
            // Trả cùng một phản hồi 404, không để lộ đường dẫn nội bộ.
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh.");
    }

    private void validateImageContent(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalArgumentException("Nội dung file không phải là ảnh hợp lệ.");
            }

            long pixels = (long) image.getWidth() * image.getHeight();
            if (pixels <= 0 || pixels > MAX_IMAGE_PIXELS) {
                throw new IllegalArgumentException("Kích thước ảnh vượt quá giới hạn cho phép.");
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Không thể đọc nội dung ảnh.", ex);
        }
    }

    private void ensureInsideStorage(Path path) {
        if (!path.startsWith(fileStorageLocation)) {
            throw new IllegalArgumentException("Đường dẫn file không hợp lệ.");
        }
    }
}
