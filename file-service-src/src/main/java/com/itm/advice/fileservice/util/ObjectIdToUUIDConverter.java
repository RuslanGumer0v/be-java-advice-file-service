package com.itm.advice.fileservice.util;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.UUID;

@ReadingConverter
public class ObjectIdToUUIDConverter implements Converter<ObjectId, UUID> {
    @Override
    public UUID convert(ObjectId source) {
        return UUID.nameUUIDFromBytes(source.toByteArray());
    }
}