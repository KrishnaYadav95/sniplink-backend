package com.example.UrlShortener.service;


import com.example.UrlShortener.model.Url;
import com.example.UrlShortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlService {
    @Autowired
    public UrlRepository repo;


    public Url save(Url url) {
       return  repo.save(url);
    }

    public void updateurlByurl(String shortUrl, String newLongUrl) {
        repo.updateByShortUrl(shortUrl, newLongUrl);
    }

    public void updateurlByid(int id, String newLongUrl) {
        System.out.println("Updating id: " + id + " with: " + newLongUrl);
        repo.updateById(id, newLongUrl);
    }

    public Url originalurl(String url) {
        return  repo.findByShorturl(url);
    }

    public Url urlbyid(int id) {
      return   repo.findById(id).orElse(null);
    }

    public void deleteurl(int id) {
        repo.deleteById(id);
    }

    public List<Url> getAllurl() {
       return  repo.findAll();
    }
}
