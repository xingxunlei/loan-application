package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private Integer id;

    private String imageType;

    private String imageFormat;

    private String image;
    
    private String imageUrl;
}