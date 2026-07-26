package com.example.UrlShortener.repository;

import com.example.UrlShortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UrlRepository extends JpaRepository<Url, Integer> {


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Url u SET u.longurl= :newLongurl WHERE u.shorturl= :shorturl")
    void updateByShortUrl(@Param("shorturl")String shorturl,@Param("newLongurl") String newLongurl);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Url u SET u.longurl= :newLongUrl WHERE u.id=:id")
    void updateById(@Param("id") int id,@Param("newLongUrl") String newLongUrl);

    Url findByShorturl(String url);
}
