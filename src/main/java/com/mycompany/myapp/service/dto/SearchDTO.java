package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Filter;
import com.mycompany.myapp.domain.Parameter;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Search} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SearchDTO implements Serializable {

    private Long id;

    private String name;

    private Boolean favorite;

    private Instant createDate;

    private UserDTO user;

    private FilterDTO filter;

    private ParameterDTO parameter;

    private Set<CommentDTO> comments = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public FilterDTO getFilter() {
        return filter;
    }

    public void setFilter(FilterDTO filter) {
        this.filter = filter;
    }

    public ParameterDTO getParameter() {
        return parameter;
    }

    public void setParameter(ParameterDTO parameter) {
        this.parameter = parameter;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchDTO)) {
            return false;
        }

        SearchDTO searchDTO = (SearchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, searchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SearchDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", favorite='" + getFavorite() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
