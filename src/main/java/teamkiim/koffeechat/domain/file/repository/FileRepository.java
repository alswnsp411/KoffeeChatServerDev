package teamkiim.koffeechat.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamkiim.koffeechat.domain.file.domain.File;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findById(Long id);

}
