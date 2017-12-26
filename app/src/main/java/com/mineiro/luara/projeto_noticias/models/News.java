package com.mineiro.luara.projeto_noticias.models;

import java.util.Date;

/**
 * Created by luara on 28/11/17.
 * Modle for table NEWS
 */

public class News {
    private static String TAG = "News";

    private long id;
    public String title;
    public String description;
    public String link;
    public String pub_date;
    public String url_image;
    public byte[] image;
    public int favorite;
    public int historic;
    public String date_historic;

    public News(String title, String link, String description, String pub_date, String url_image, byte[] image, int favorite, int historic, String date_historic) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pub_date = pub_date;
        this.url_image = url_image;
        this.image = image;
        this.favorite = favorite;
        this.historic = historic;
        this.date_historic = date_historic;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public void setHistoric(int historic) {
        this.historic = historic;
    }
}