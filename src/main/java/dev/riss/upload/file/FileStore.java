package dev.riss.upload.file;

import dev.riss.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath (String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles (List<MultipartFile> multipartFiles) {
        List<UploadFile> storeFileResult=new ArrayList<>();

        multipartFiles.stream().forEach(f -> {
            if (!f.isEmpty()) {
                try {
                    storeFileResult.add(storeFile(f));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return storeFileResult;
    }

    public UploadFile storeFile (MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) return null;

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        File dest = new File(getFullPath(storeFileName));
        dest.getParentFile().mkdirs();
        multipartFile.transferTo(dest);
        
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        // image.png -> 확장자 가져오기
        String ext = extractExt(originalFilename);

        // 서버에 저장할 파일명
        String uuid = UUID.randomUUID().toString();
        
        // "qwe-qwe-qwe-123-123-123.png";
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
