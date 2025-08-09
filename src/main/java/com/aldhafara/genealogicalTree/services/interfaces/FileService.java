package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.IllegalFileFormatException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void analyze(MultipartFile file) throws IllegalFileFormatException;
}
