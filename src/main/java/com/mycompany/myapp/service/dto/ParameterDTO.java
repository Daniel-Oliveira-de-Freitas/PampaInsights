package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Parameter} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParameterDTO implements Serializable {

    private Long id;

    private String terms;

    private String webSite;

    private String instagram;

    private String facebook;

    private String linkedin;

    private String x;

    private Instant createDate;

    private SearchDTO search;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public SearchDTO getSearch() {
        return search;
    }

    public void setSearch(SearchDTO search) {
        this.search = search;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterDTO)) {
            return false;
        }

        ParameterDTO parameterDTO = (ParameterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parameterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParameterDTO{" +
            "id=" + getId() +
            ", terms='" + getTerms() + "'" +
            ", webSite='" + getWebSite() + "'" +
            ", instagram='" + getInstagram() + "'" +
            ", facebook='" + getFacebook() + "'" +
            ", linkedin='" + getLinkedin() + "'" +
            ", x='" + getX() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", search=" + getSearch() +
            "}";
    }
}
