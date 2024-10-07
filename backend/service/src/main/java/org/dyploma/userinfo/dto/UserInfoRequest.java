package org.dyploma.userinfo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserInfoRequest (@NotEmpty String firstName, @NotEmpty String lastName, @Email String email) {}
