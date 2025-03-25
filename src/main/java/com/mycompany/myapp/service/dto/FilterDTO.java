package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Emotions;
import com.mycompany.myapp.domain.enumeration.TypeOfChart;
import com.mycompany.myapp.domain.enumeration.Visualization;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Filter} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FilterDTO implements Serializable {

    private Long id;

    private String name;

    private Visualization visualization;

    private TypeOfChart typeOfChart;

    private Emotions emotions;

    private SearchDTO search;

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

    public Visualization getVisualization() {
        return visualization;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    public TypeOfChart getTypeOfChart() {
        return typeOfChart;
    }

    public void setTypeOfChart(TypeOfChart typeOfChart) {
        this.typeOfChart = typeOfChart;
    }

    public Emotions getEmotions() {
        return emotions;
    }

    public void setEmotions(Emotions emotions) {
        this.emotions = emotions;
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
        if (!(o instanceof FilterDTO)) {
            return false;
        }

        FilterDTO filterDTO = (FilterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, filterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilterDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", visualization='" + getVisualization() + "'" +
            ", typeOfChart='" + getTypeOfChart() + "'" +
            ", emotions='" + getEmotions() + "'" +
            ", search=" + getSearch() +
            "}";
    }
}
