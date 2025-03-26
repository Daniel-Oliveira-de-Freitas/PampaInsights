package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Search.
 */
@Entity
@Table(name = "search")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "favorite")
    private Boolean favorite;

    @Column(name = "create_date")
    private Instant createDate;

    @JsonIgnoreProperties(value = { "search" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "search")
    private Filter filter;

    @JsonIgnoreProperties(value = { "search" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "search")
    private Parameter parameter;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "search")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "search" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Search id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Search name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFavorite() {
        return this.favorite;
    }

    public Search favorite(Boolean favorite) {
        this.setFavorite(favorite);
        return this;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public Search createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        if (this.filter != null) {
            this.filter.setSearch(null);
        }
        if (filter != null) {
            filter.setSearch(this);
        }
        this.filter = filter;
    }

    public Search filter(Filter filter) {
        this.setFilter(filter);
        return this;
    }

    public Parameter getParameter() {
        return this.parameter;
    }

    public void setParameter(Parameter parameter) {
        if (this.parameter != null) {
            this.parameter.setSearch(null);
        }
        if (parameter != null) {
            parameter.setSearch(this);
        }
        this.parameter = parameter;
    }

    public Search parameter(Parameter parameter) {
        this.setParameter(parameter);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setSearch(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setSearch(this));
        }
        this.comments = comments;
    }

    public Search comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Search addComment(Comment comment) {
        this.comments.add(comment);
        comment.setSearch(this);
        return this;
    }

    public Search removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setSearch(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Search user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Search)) {
            return false;
        }
        return getId() != null && getId().equals(((Search) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Search{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", favorite='" + getFavorite() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            "}";
    }
}
