package com.example.generated.model;

import java.net.URI;
import java.util.Objects;
import com.example.generated.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UserInfoPageElements
 */

@JsonTypeName("userInfoPageElements")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-02T17:49:04.935388930+02:00[Europe/Warsaw]")
public class UserInfoPageElements {

  @JsonProperty("content")
  @Valid
  private List<UserInfo> content = null;

  public UserInfoPageElements content(List<UserInfo> content) {
    this.content = content;
    return this;
  }

  public UserInfoPageElements addContentItem(UserInfo contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

  /**
   * Get content
   * @return content
  */
  @Valid 
  @Schema(name = "content", required = false)
  public List<UserInfo> getContent() {
    return content;
  }

  public void setContent(List<UserInfo> content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfoPageElements userInfoPageElements = (UserInfoPageElements) o;
    return Objects.equals(this.content, userInfoPageElements.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoPageElements {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

