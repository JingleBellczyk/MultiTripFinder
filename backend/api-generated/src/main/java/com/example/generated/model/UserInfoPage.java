package com.example.generated.model;

import java.net.URI;
import java.util.Objects;
import com.example.generated.model.PageInfo;
import com.example.generated.model.Paged;
import com.example.generated.model.UserInfo;
import com.example.generated.model.UserInfoPageElements;
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
 * UserInfoPage
 */

@JsonTypeName("userInfoPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-02T17:49:04.935388930+02:00[Europe/Warsaw]")
public class UserInfoPage {

  @JsonProperty("page")
  private PageInfo page;

  @JsonProperty("sort")
  @Valid
  private List<String> sort = null;

  @JsonProperty("totalElements")
  private Integer totalElements;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("first")
  private Boolean first;

  @JsonProperty("last")
  private Boolean last;

  @JsonProperty("empty")
  private Boolean empty;

  @JsonProperty("content")
  @Valid
  private List<UserInfo> content = null;

  public UserInfoPage page(PageInfo page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
  */
  @Valid 
  @Schema(name = "page", required = false)
  public PageInfo getPage() {
    return page;
  }

  public void setPage(PageInfo page) {
    this.page = page;
  }

  public UserInfoPage sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public UserInfoPage addSortItem(String sortItem) {
    if (this.sort == null) {
      this.sort = new ArrayList<>();
    }
    this.sort.add(sortItem);
    return this;
  }

  /**
   * Get sort
   * @return sort
  */
  
  @Schema(name = "sort", required = false)
  public List<String> getSort() {
    return sort;
  }

  public void setSort(List<String> sort) {
    this.sort = sort;
  }

  public UserInfoPage totalElements(Integer totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Get totalElements
   * @return totalElements
  */
  
  @Schema(name = "totalElements", required = false)
  public Integer getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Integer totalElements) {
    this.totalElements = totalElements;
  }

  public UserInfoPage totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * @return totalPages
  */
  
  @Schema(name = "totalPages", required = false)
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public UserInfoPage first(Boolean first) {
    this.first = first;
    return this;
  }

  /**
   * Get first
   * @return first
  */
  
  @Schema(name = "first", required = false)
  public Boolean getFirst() {
    return first;
  }

  public void setFirst(Boolean first) {
    this.first = first;
  }

  public UserInfoPage last(Boolean last) {
    this.last = last;
    return this;
  }

  /**
   * Get last
   * @return last
  */
  
  @Schema(name = "last", required = false)
  public Boolean getLast() {
    return last;
  }

  public void setLast(Boolean last) {
    this.last = last;
  }

  public UserInfoPage empty(Boolean empty) {
    this.empty = empty;
    return this;
  }

  /**
   * Get empty
   * @return empty
  */
  
  @Schema(name = "empty", required = false)
  public Boolean getEmpty() {
    return empty;
  }

  public void setEmpty(Boolean empty) {
    this.empty = empty;
  }

  public UserInfoPage content(List<UserInfo> content) {
    this.content = content;
    return this;
  }

  public UserInfoPage addContentItem(UserInfo contentItem) {
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
    UserInfoPage userInfoPage = (UserInfoPage) o;
    return Objects.equals(this.page, userInfoPage.page) &&
        Objects.equals(this.sort, userInfoPage.sort) &&
        Objects.equals(this.totalElements, userInfoPage.totalElements) &&
        Objects.equals(this.totalPages, userInfoPage.totalPages) &&
        Objects.equals(this.first, userInfoPage.first) &&
        Objects.equals(this.last, userInfoPage.last) &&
        Objects.equals(this.empty, userInfoPage.empty) &&
        Objects.equals(this.content, userInfoPage.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, sort, totalElements, totalPages, first, last, empty, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoPage {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    first: ").append(toIndentedString(first)).append("\n");
    sb.append("    last: ").append(toIndentedString(last)).append("\n");
    sb.append("    empty: ").append(toIndentedString(empty)).append("\n");
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

