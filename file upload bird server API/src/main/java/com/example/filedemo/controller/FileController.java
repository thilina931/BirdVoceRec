package com.example.filedemo.controller;

import audioMatch.Videotoaudio;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import audioMatch.fingerprint;
import database.DBconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
public class FileController {
    Connection connect =null;
    ResultSet rst=null;
    PreparedStatement pst=null;
    float score[] = new float[50];
    String filename[]= new String[50];
    String descrption[]= new String[50];
    String imageUrl[]= new String[50];
UploadFileResponse nup;
String match="";
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
///match result
        Videotoaudio.ExtractAudio("C:\\Users\\Thilina\\Desktop\\ttt\\research\\file upload bird server\\uploads\\" +fileName);
//            Videotoaudio.ExtractAudio("C:\\Users\\Thilina\\Desktop\\Bird\\Greater Flameback.mp3");
        String main="C:\\Users\\Thilina\\Desktop\\ttt\\research\\file upload bird server\\converted\\converted.wav";
        
        String que= "select * from bird";
        try {
            System.out.println("com.example.filedemo.controller.FileController.uploadFile()");
            connect = DBconnect.connect();
            
            pst= connect.prepareStatement(que);
            rst= pst.executeQuery();
            int x=0;
            while (rst.next()){
                String filepath= "C:\\Users\\Thilina\\Desktop\\Bird\\"+rst.getString(2)+ ".wav";
                System.out.println(score.length);
                score[x]= fingerprint.fingerprinting(main, filepath);
                filename[x]=rst.getString("Bird Name");
                descrption[x]=rst.getString("Bird Detils");
                imageUrl[x]=rst.getString("Bird Image");
                x++;
              
            }
            int maxIndex = fingerprint.max(score);
            if (score[maxIndex]>1){
                match="1";
                System.out.println("max score: "+ score[maxIndex]);
                System.out.println("filename: "+ filename[maxIndex]);
                System.out.println("descrpition: "+ descrption[maxIndex]);
                
                System.out.println("url: "+ imageUrl[maxIndex]);
                nup = new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize(),match,imageUrl[maxIndex],descrption[maxIndex]);
            
            }
            else{
                match="0";
                nup = new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize(),match,imageUrl[maxIndex],descrption[maxIndex]);
            
                System.out.println("Not mached");
            }
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return  nup;
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
