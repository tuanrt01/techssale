package com.techzen.techsale.utils;

import com.techzen.techsale.enumeration.SortFieldRequestEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortFieldEnumConverter implements Converter<String, SortFieldRequestEnum> {

    @Override
    public SortFieldRequestEnum convert(String source) {
        try {
            return SortFieldRequestEnum.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SortFieldRequestEnum.ID;
        }
    }
}