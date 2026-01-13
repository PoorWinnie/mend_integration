package com.example.back.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * Page 對象序列化自定義組件
 * 使用 Spring Boot 的 @JsonComponent 註解來自動註冊到 Jackson
 */
@JsonComponent
public class PageSerializationCustomizer {

    /**
     * Page 對象的 JSON 序列化器
     */
    public static class PageSerializer extends JsonSerializer<Page<?>> {
        @Override
        public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("content", page.getContent());
            gen.writeNumberField("totalPages", page.getTotalPages());
            gen.writeNumberField("totalElements", page.getTotalElements());
            gen.writeNumberField("size", page.getSize());
            gen.writeNumberField("number", page.getNumber());
            gen.writeBooleanField("first", page.isFirst());
            gen.writeBooleanField("last", page.isLast());
            gen.writeNumberField("numberOfElements", page.getNumberOfElements());
            gen.writeBooleanField("empty", page.isEmpty());
            gen.writeEndObject();
        }

        @Override
        public Class<Page<?>> handledType() {
            return (Class<Page<?>>) (Class<?>) Page.class;
        }
    }
}