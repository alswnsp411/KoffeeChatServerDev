package teamkiim.koffeechat.domain.file.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamkiim.koffeechat.domain.file.domain.File;
import teamkiim.koffeechat.domain.file.domain.PostFile;
import teamkiim.koffeechat.domain.file.dto.response.ImageUrlResponse;
import teamkiim.koffeechat.domain.file.repository.FileRepository;
import teamkiim.koffeechat.domain.file.repository.PostFileRepository;
import teamkiim.koffeechat.domain.file.service.FileStorageService;
import teamkiim.koffeechat.domain.file.service.PostFileService;
import teamkiim.koffeechat.domain.post.common.domain.Post;
import teamkiim.koffeechat.domain.post.common.repository.PostRepository;
import teamkiim.koffeechat.global.exception.CustomException;
import teamkiim.koffeechat.global.exception.ErrorCode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile("dev")
public class S3PostFileService implements PostFileService {

    private final FileRepository fileRepository;
    private final PostFileRepository postFileRepository;
    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    /**
     * 이미지 파일 S3에 단건 저장
     *
     * @param multipartFile 실제 파일
     * @param postId        연관 게시물 PK
     * @return ImagePathResponse
     */
    public ImageUrlResponse uploadImageFile(MultipartFile multipartFile, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        ImageUrlResponse imageUrlResponse = fileStorageService.uploadFile(fileName, multipartFile);

        PostFile saveFile = postFileRepository.save(new PostFile(imageUrlResponse.getUrl(), post));

        post.addPostFile(saveFile);                         // 양방향 연관관계 주입

        return imageUrlResponse;
    }

    /**
     * 게시물에 연관된 이미지 파일 모두 삭제 (게시글 작성 취소 시 호출)
     *
     * @param post 연관 게시물
     */
    @Transactional
    public void deleteImageFiles(Post post) {

        List<PostFile> fileList = postFileRepository.findAllByPost(post);

        for (PostFile postFile : fileList) {
            fileStorageService.deleteFile(postFile.getUrl());
        }

        fileRepository.deleteAll(fileList);
    }

    /**
     * 이미지 파일 다건 삭제 (post에 연관된 File 중 id값이 fileIdList에 없는 File 삭제)
     *
     * @param fileIdList 삭제하지 않을 이미지 파일 id 리스트
     * @param post       연관 게시물
     * @return
     */
    @Transactional
    public void deleteImageFiles(List<Long> fileIdList, Post post) {

        List<PostFile> existFileList = postFileRepository.findAllByPost(post);

        List<File> deleteFileList = existFileList.stream()
                .filter(file -> !fileIdList.contains(file.getId()))
                .collect(Collectors.toList());

        fileStorageService.deleteFiles(deleteFileList);

        fileRepository.deleteAll(deleteFileList);
    }
}
