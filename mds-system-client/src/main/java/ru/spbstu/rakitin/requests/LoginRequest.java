package ru.spbstu.rakitin.requests;

import lombok.Builder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import ru.spbstu.rakitin.ApiName;
import ru.spbstu.rakitin.MdsRequest;
import ru.spbstu.rakitin.dto.ParametrizedTypes;
import ru.spbstu.rakitin.dto.AuthUserDto;

@Builder
public class LoginRequest extends MdsRequest<AuthUserDto, String> {

    private final String username;
    private final String password;


    @Override
    public ApiName apiName() {
        return ApiName.AUTH;
    }

    @Override
    public ParameterizedTypeReference<String> getResponseClass() {
        return ParametrizedTypes.STRING_TYPE;
    }

    @Override
    public boolean needAuthentication() {
        return false;
    }

    @Override
    public AuthUserDto getBody() {
        return AuthUserDto.builder().username(username).password(password).build();
    }

    @Override
    public boolean hasResponse() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String buildPath() {
        return "/login";
    }
}
