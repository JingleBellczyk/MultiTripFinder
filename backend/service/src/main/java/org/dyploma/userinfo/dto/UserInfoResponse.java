package org.dyploma.userinfo.dto;

import java.util.UUID;

public record UserInfoResponse(UUID id, String firstName, String lastName, String email) {}
