package com.project.controller;

import com.project.model.Image;
import com.project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ClientController {
    @Autowired
    private ImageService imageService;

    @GetMapping("/ping")
    @ResponseBody
    public String hello_world(){
        return "Hello World!";
    }

    // display image
    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException
    {
        Image image = imageService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = image.getImage().getBytes(1,(int) image.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    
 // display image
    @GetMapping("/download")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id, HttpServletResponse response) throws IOException, SQLException {
        Image image = imageService.viewById(id);
        byte[] imageBytes = image.getImage().getBytes(1, (int) image.getImage().length());
        
        // set headers for the response
        response.setContentType("image/jpeg");
//        response.setContentType(image.getContentType);
        response.setContentLength(imageBytes.length);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=image_" + id + ".jpg";
        response.setHeader(headerKey, headerValue);
        
        // write image to response output stream
        response.getOutputStream().write(imageBytes);
        response.getOutputStream().flush();
        
        return null;
    }


    // view All images
    @GetMapping("/")
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView("index");
        List<Image> imageList = imageService.viewAll();
        mv.addObject("imageList", imageList);
        return mv;
    }

    // add image - get
    @GetMapping("/add")
    public ModelAndView addImage(){
        return new ModelAndView("addimage");
    }

    // add image - post
    @PostMapping("/add")
    public String addImagePost(HttpServletRequest request,@RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException
    {
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
        String type = file.getContentType();
        System.out.println("image type is : " + type);

        Image image = new Image();
        image.setImage(blob);
        imageService.create(image);
        return "redirect:/";
    }
}