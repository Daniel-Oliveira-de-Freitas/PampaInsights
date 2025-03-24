package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
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
