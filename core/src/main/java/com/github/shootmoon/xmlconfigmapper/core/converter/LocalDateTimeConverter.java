package com.github.shootmoon.xmlconfigmapper.core.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class LocalDateTimeConverter implements TypeConverter<LocalDateTime>
{
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime read(String value)
    {
        return LocalDateTime.parse(value, formatter);
    }
}
