package dev.biddan.jsonapiexample.jsonapi;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.toedter.spring.hateoas.jsonapi.JsonApiConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonApiConfig {

    @Bean
    JsonApiConfiguration jsonApiConfiguration() {
        return new JsonApiConfiguration()
                // 다른 설정들은 필요에 따라 추가
                .withObjectMapperCustomizer(objectMapper -> {
                    // JavaTimeModule 등록
                    objectMapper.registerModule(new JavaTimeModule());
                    // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화
                    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                });
    }
}
