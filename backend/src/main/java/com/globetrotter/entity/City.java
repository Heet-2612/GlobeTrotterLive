package com.globetrotter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(name = "cost_index", nullable = false)
    private Double costIndex;

    @Column(nullable = false)
    private Integer popularity;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public City() {
    }

    public City(Long id, String name, String country, String region, Double costIndex, Integer popularity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.region = region;
        this.costIndex = costIndex;
        this.popularity = popularity;
        this.imageUrl = imageUrl;
    }

    public static CityBuilder builder() {
        return new CityBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Double getCostIndex() { return costIndex; }
    public void setCostIndex(Double costIndex) { this.costIndex = costIndex; }

    public Integer getPopularity() { return popularity; }
    public void setPopularity(Integer popularity) { this.popularity = popularity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public static class CityBuilder {
        private Long id;
        private String name;
        private String country;
        private String region;
        private Double costIndex;
        private Integer popularity;
        private String imageUrl;

        public CityBuilder id(Long id) { this.id = id; return this; }
        public CityBuilder name(String name) { this.name = name; return this; }
        public CityBuilder country(String country) { this.country = country; return this; }
        public CityBuilder region(String region) { this.region = region; return this; }
        public CityBuilder costIndex(Double costIndex) { this.costIndex = costIndex; return this; }
        public CityBuilder popularity(Integer popularity) { this.popularity = popularity; return this; }
        public CityBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public City build() {
            return new City(id, name, country, region, costIndex, popularity, imageUrl);
        }
    }
}
