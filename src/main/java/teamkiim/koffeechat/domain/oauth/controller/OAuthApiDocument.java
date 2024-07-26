package teamkiim.koffeechat.domain.oauth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "OAuth 인증 API")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuthApiDocument {
}
