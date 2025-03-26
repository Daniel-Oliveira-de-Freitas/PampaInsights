package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Emotions;
import com.mycompany.myapp.domain.enumeration.TypeOfChart;
import com.mycompany.myapp.domain.enumeration.Visualization;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Filter.
 */
@Entity
@Table(name = "filter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Filter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "visualization")
    private Visualization visualization;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_chart")
    private TypeOfChart typeOfChart;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotions")
    private Emotions emotions;

    @JsonIgnoreProperties(value = { "filter", "parameter", "comments", "user" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Search search;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Filter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Filter name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public Filter visualization(Visualization visualization) {
        this.setVisualization(visualization);
        return this;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public TypeOfChart getTypeOfChart() {
        return this.typeOfChart;
    }

    public Filter typeOfChart(TypeOfChart typeOfChart) {
        this.setTypeOfChart(typeOfChart);
        return this;
    }

    public void setTypeOfChart(TypeOfChart typeOfChart) {
        this.typeOfChart = typeOfChart;
    }

    public Emotions getEmotions() {
        return this.emotions;
    }

    public Filter emotions(Emotions emotions) {
        this.setEmotions(emotions);
        return this;
    }

    public void setEmotions(Emotions emotions) {
        this.emotions = emotions;
    }

    public Search getSearch() {
        return this.search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Filter search(Search search) {
        this.setSearch(search);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Filter)) {
            return false;
        }
        return getId() != null && getId().equals(((Filter) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Filter{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", visualization='" + getVisualization() + "'" +
            ", typeOfChart='" + getTypeOfChart() + "'" +
            ", emotions='" + getEmotions() + "'" +
            "}";
    }
}
