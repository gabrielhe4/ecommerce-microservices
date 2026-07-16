package io.github.gabrielhe4.product_service.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.gabrielhe4.product_service.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        
        String originalFilename = file.getOriginalFilename();
        
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(
            originalFilename.substring(
                originalFilename.indexOf(randomId)
            )
        );

        String filePath = path + File.separator + fileName;
        File folder = new File(path);

        if (!folder.exists())
            folder.mkdir();

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return filePath;
        
    }

}
