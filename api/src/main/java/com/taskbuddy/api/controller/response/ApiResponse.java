package com.taskbuddy.api.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.util.Assert;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiResponse<D> {
    private final ResultStatus status;
    private final D data;
    private final ErrorDetails error;

    private ApiResponse(ResultStatus status, D data, ErrorDetails error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <D> ApiResponse<D> success(D data) {
        return new ApiResponse<>(ResultStatus.SUCCESS, data, null);
    }

    public static ApiResponse<?> fail(ErrorDetails error) {
        Assert.notNull(error, "The error argument must not be null.");

        return new ApiResponse<>(ResultStatus.FAIL, null, error);
    }
}
