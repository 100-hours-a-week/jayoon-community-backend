package kr.adapterz.community.domain.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import kr.adapterz.community.common.exception.dto.BadRequestException;
import kr.adapterz.community.common.message.ErrorCode;
import kr.adapterz.community.domain.image.dto.PreSignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ImageService {

    private final String uploadDir;
    private final String baseUrl;

    public ImageService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.base-url}") String baseUrl
    ) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;

        // 애플리케이션 시작 시 업로드 디렉터리 생성
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            // 실무에서는 Log.error() 등을 사용하여 로깅하는 것이 좋습니다.
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    /**
     * 수정용 "Pre-Signed URL"과 조회용 "이미지 URL" 생성
     *
     * @param filename    원본 파일명
     * @param contentType 파일의 Mime-Type
     * @return PreSignedUrlResponse
     */
    public PreSignedUrlResponse getPreSignedUrl(String filename, String contentType) {
        // 1. 파일 확장자 추출
        String extension = StringUtils.getFilenameExtension(filename);

        // 2. 고유한 파일명 생성 (profile/UUID.extension)
        // 여기서는 간단하게 "profile" 하위 경로로 고정합니다.
        String uniqueFilename =
                UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
        String relativePath = "profile/" + uniqueFilename;

        // 3. 업로드할 URL (PUT /api/images?path=...)
        String preSignedUrl = baseUrl + "/images?path=" + relativePath;

        // 4. 업로드 완료 후 접근할 최종 URL (GET /images/...)
        String imageUrl = baseUrl + "/images/" + relativePath;

        return new PreSignedUrlResponse(preSignedUrl, imageUrl);
    }

    /**
     * 파일을 로컬 스토리지에 저장
     *
     * @param path        파일을 저장할 상대 경로 (getPreSignedUrl에서 생성한 값)
     * @param inputStream 업로드된 파일의 스트림
     * @throws IOException
     */
    public void uploadImage(String path, InputStream inputStream) throws IOException {
        Path rootLocation = Paths.get(this.uploadDir).toAbsolutePath().normalize();
        Path destinationFile = rootLocation.resolve(path).normalize();

        // **보안 검사:** path가 상위 디렉터리로 이동하는 것을 방지 (Directory Traversal)
        if (!destinationFile.startsWith(rootLocation)) {
            throw new BadRequestException(ErrorCode.INVALID_IMAGE_PATH);
        }

        // 1. 필요한 하위 디렉터리 생성
        Files.createDirectories(destinationFile.getParent());

        // 2. 파일 저장
        // try-with-resources를 사용하여 inputStream을 자동으로 닫습니다.
        try (InputStream is = inputStream) {
            Files.copy(is, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}