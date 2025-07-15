package com.github.goodluckwu.onepiece.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * @see <a href="https://blog.csdn.net/f641385712/article/details/97402946">参数验证</a>
 */
@Validated
public interface HelloService {
    @NotBlank
    @NotNull
    @NotEmpty
    String hello(@NotNull @Min(10) Integer id, @NotBlank String name);

    @NotNull
    @Valid
    Person cascade(@NotNull @Valid Person father, @NotNull Person mother);
}
