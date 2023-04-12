package dev.riss.upload.controller;

import dev.riss.upload.domain.Item;
import dev.riss.upload.domain.ItemRepository;
import dev.riss.upload.domain.UploadFile;
import dev.riss.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem (@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem (@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        // store file in storage
        UploadFile storeAttachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // store metadata of file in database
        Item savedItem = itemRepository.save(new Item(null, form.getItemName(), storeAttachFile, storeImageFiles));

        redirectAttributes.addAttribute("itemId", savedItem.getId());

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items (@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);

        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage (@PathVariable String filename) throws MalformedURLException {
        // ex: "file:c/data/practice/9207a0e7-a0fa-4176-981c-bab919325a02.png"
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach (@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        // filename 이 한글이면 깨질 수도 때문에 인코딩해줘야함 (안해주면 오류나고, 값이 invalid 해서 헤더에 content-disposition 안붙음)
        String encodedUploadFilename = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        String contentDisposition="attachment; filename=\"" + encodedUploadFilename + "\"";
        // Content-Disposition 에 attachment; filename="~~~" 가 있어야 이걸 보고 브라우저가 다운로드를 시킴
        // 그렇지 않으면 inputStream (binary code) 문자열이 보인다 (text file 이라면 안의 내용이 브라우저에서 보이기만 함)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
