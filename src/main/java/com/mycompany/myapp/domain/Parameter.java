package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Parameter.
 */
@Entity
@Table(name = "parameter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "terms")
    private String terms;

    @Column(name = "web_site")
    private String webSite;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "x")
    private String x;

    @Column(name = "create_date")
    private Instant createDate;

    @JsonIgnoreProperties(value = { "filter", "parameter", "comments", "user" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Search search;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Parameter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerms() {
        return this.terms;
    }

    public Parameter terms(String terms) {
        this.setTerms(terms);
        return this;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getWebSite() {
        return this.webSite;
    }

    public Parameter webSite(String webSite) {
        this.setWebSite(webSite);
        return this;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getInstagram() {
        return this.instagram;
    }

    public Parameter instagram(String instagram) {
        this.setInstagram(instagram);
        return this;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return this.facebook;
    }

    public Parameter facebook(String facebook) {
        this.setFacebook(facebook);
        return this;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedin() {
        return this.linkedin;
    }

    public Parameter linkedin(String linkedin) {
        this.setLinkedin(linkedin);
        return this;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getX() {
        return this.x;
    }

    public Parameter x(String x) {
        this.setX(x);
        return this;
    }

    public void setX(String x) {
        this.x = x;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public Parameter createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Search getSearch() {
        return this.search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Parameter search(Search search) {
        this.setSearch(search);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parameter)) {
            return false;
        }
        return getId() != null && getId().equals(((Parameter) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parameter{" +
            "id=" + getId() +
            ", terms='" + getTerms() + "'" +
            ", webSite='" + getWebSite() + "'" +
            ", instagram='" + getInstagram() + "'" +
            ", facebook='" + getFacebook() + "'" +
            ", linkedin='" + getLinkedin() + "'" +
            ", x='" + getX() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            "}";
    }
}
