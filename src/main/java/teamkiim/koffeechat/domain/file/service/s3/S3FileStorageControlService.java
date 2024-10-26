package teamkiim.koffeechat.domain.file.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.service.FileStorageService;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * S3 물리적 파일 I/O 담당
 */
@Component
@RequiredArgsConstructor
@Profile("prod")
public class S3FileStorageControlService implements FileStorageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${domain-name}")
    private String domainName;

    /**
     * 파일 S3에 단건 저장
     *
     * @param multipartFile 실제 파일
     */
    public ImageUrlResponse uploadFile(String fileName, MultipartFile multipartFile) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        try {
            amazonS3Client.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        } catch (IllegalStateException e) {
            throw new CustomException(ErrorCode.FILE_REQUEST_ERROR);
        }

        return ImageUrlResponse.of("https://" + domainName + File.separator + fileName);
    }

    /**
     * 파일 단건 삭제
     *
     * @param url
     */
    public void deleteFile(String url) {

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, url);

        try {
            amazonS3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        }
    }

    /**
     * 파일 다건 삭제
     *
     * @param fileList
     */
    public void deleteFiles(List<teamkiim.koffeechat.domain.file.domain.File> fileList) {

        List<DeleteObjectsRequest.KeyVersion> keysToDelete = fileList.stream()
                .map(file -> new DeleteObjectsRequest.KeyVersion(file.getUrl()))
                .toList();

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keysToDelete);

        try {
            amazonS3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_IO_FAILED);
        }

    }
}