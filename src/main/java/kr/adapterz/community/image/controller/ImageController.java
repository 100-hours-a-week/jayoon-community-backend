package kr.adapterz.community.image.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import kr.adapterz.community.common.message.SuccessCode;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.image.dto.PreSignedUrlResponse;
import kr.adapterz.community.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 수정용 pre-signed URL과 조회용 이미지 URL을 조회합니다.
     * <p>
     * S3를 도입하기 전에는 클라이언트가 pre-signed URL을 통해 PUT /images?path= 로 저장하고 이미지 URL을 통해 조회하여 가져옵니다.
     */
    @GetMapping("/pre-signed-url")
    public ResponseEntity<ApiResponseDto<PreSignedUrlResponse>> getPreSignedUrl(
            @RequestParam String filename,
            @RequestParam("content-type") String contentType
    ) {
        PreSignedUrlResponse response = imageService.getPreSignedUrl(filename, contentType);
        ApiResponseDto<PreSignedUrlResponse> responseBody = ApiResponseDto.success(response, "Pre"
                + " signed URL 조회를 완료했습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 이미지 업로드 (PUT) 프론트엔드가 /pre-signed-url 에서 받은 preSignedUrl 주소로 PUT 요청을 보냅니다. IOException 발생 시
     * GlobalExceptionHandler가 처리합니다.
     */
    @PutMapping
    public ResponseEntity<ApiResponseDto<Void>> uploadImage(
            @RequestParam String path,
            HttpServletRequest request
    ) throws IOException { // IOException Checked Exception이라서 메서드에 직접 표시합니다.

        imageService.uploadImage(path, request.getInputStream());

        ApiResponseDto<Void> responseBody = ApiResponseDto.success(null,
                SuccessCode.IMAGE_UPLOAD_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}