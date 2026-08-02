package com.example.UrlShortener.controller;

import com.example.UrlShortener.model.Url;
import com.example.UrlShortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/url")
public class UrlController {

    @Autowired
    public UrlService service;

    @PostMapping("/short-url")
    public Url shortUrl(@RequestBody Url url){
        String shortcode= UUID.randomUUID().toString().substring(0,8);
        System.out.println("short url created");
          url.setShorturl(shortcode);
        System.out.println("short url set in service");
        return service.save(url);
    }
    @GetMapping("/original-url/{shorturl}")
    public ResponseEntity<Void> originalurl(@PathVariable String shorturl){
        Url url= service.originalurl(shorturl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.getLongurl()))
                .build();
    }
    @GetMapping("/url-id/{id}")
    public Url urlbyid(@PathVariable int id){
        return service.urlbyid(id);
    }
    @PutMapping("/update/{url}")
    public void updateurlByurl(@PathVariable String url ,@RequestBody String newLongUrl){
        service.updateurlByurl(url , newLongUrl);
    }

    @PutMapping("/update-urlid/{id}")
    public void updateurlByid(@PathVariable int id , @RequestBody String newLongUrl){
        service.updateurlByid(id ,newLongUrl);
    }
    @DeleteMapping("delete-url/{id}")
    public void deleteurl(@PathVariable int id){
        service.deleteurl(id);
    }

    @GetMapping("/allurl")
    public List<Url> getAllurl(){
        return  service.getAllurl();
    }
}
