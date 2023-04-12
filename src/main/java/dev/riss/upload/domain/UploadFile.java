package dev.riss.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadFile {

    private String uploadFileName;
    private String storeFileName;   // 고객이 저장한 파일명이 겹칠 수 있기에 내부적으로 관리하는 유니크한 파일명 필요

}
