package org.dyploma.useraccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAccountRequest {
    private String email;
}
