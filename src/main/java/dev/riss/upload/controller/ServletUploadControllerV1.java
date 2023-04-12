package dev.riss.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadControllerV1 {

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1 (HttpServletRequest request) throws ServletException, IOException {
        log.info("[ControllerV1] request={}", request);
        //org.springframework.web.multipart.support.StandardMultipartHttpServletRequest@xxx (CGRIB) <= 멀티파트 데이터 처리하는 요청객체
        // 기본 application/x-www-form-urlencoded 는 org.apache.catalina.connector.RequestFacade@xxx (기본 톰캣꺼 사용)를 썼는데 다른애로 바껴있음

        // StandardMultipartHttpServletRequest 는 multipart 형태의 요청일 때, 그 안의 단순한 인풋 데이터들을 가져올 수 있음 (파일 바이너리는 제외)
        // RequestFacade 를 쓰도록 멀티파트 관련 처리 기능을 꺼버리면(application.properties), 값이 안들어옴 null 로 뜸
        String itemName = request.getParameter("itemName");
        log.info("[ControllerV1] itemName={}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("[ControllerV1] parts={}", parts);

        return "upload-form";

        /* ** spring.servlet.multipart.enabled=true 동작 원리
         * 1. DispatcherServlet 에서 멀티파트 리졸버(MultipartResolver) 를 실행
         * 2. MultipartResolver 는 멀티파트 요청인 경우에 한해서 서블릿 컨테이너가 전달하는 일반적인 HttpServletRequest 를
         *    MultipartHttpServletRequest 로 변환해서 반환 (HttpServletRequest 의 자식 인터페이스, 멀티파트와 관련 추가 기능 제공)
         *    (스프링은 MultipartHttpServletRequest 인터페이스의 구현체인 StandardMultipartServletRequest 를 멀티파트 리졸버로 기본 제공)
         * 3. 이제 컨트롤러에서 HttpServletRequest 대신 MultipartHttpServletRequest 를 주입받을 수 있음.
         *    이 구현체를 이용하면 멀티파트와 관련된 여러가지 처리를 편하게 가능
         *    (but, 이후 설명할 MultipartFile 이라는 것을 사용하는 것이 더 편해서 이 구현체를 잘 사용하지 않음 => MultipartResolver 검색)
         */
    }
}
