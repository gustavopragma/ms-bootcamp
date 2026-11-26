package com.onclass.ms_bootcamp.domain.model;

import java.time.LocalDate;
import java.util.List;

public class Bootcamp {
    private Long id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer duration;
    private List<Long> capabilityIds;
    private List<Capability> capabilities;

    public Bootcamp(
        Long id,
        String name,
        String description,
        LocalDate launchDate,
        Integer duration,
        List<Long> capabilityIds,
        List<Capability> capabilities
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.launchDate = launchDate;
        this.duration = duration;
        this.capabilityIds = capabilityIds;
        this.capabilities = capabilities;
    }

    public Bootcamp() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Long> getCapabilityIds() {
        return capabilityIds;
    }

    public void setCapabilityIds(List<Long> capabilityIds) {
        this.capabilityIds = capabilityIds;
    }
}
