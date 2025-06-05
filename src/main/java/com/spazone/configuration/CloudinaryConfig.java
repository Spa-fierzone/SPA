package com.spazone.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnm1hfesq",
                "api_key", "711887429381317",
                "api_secret", "7Oj_DGIcuuGdl02xg18G4pVn_U8",
                "secure", true));
    }
}

