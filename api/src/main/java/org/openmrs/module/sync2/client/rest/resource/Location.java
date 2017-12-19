package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.context.Context;

import java.util.List;

public class Location implements RestResource {
    private String uuid;
    private String display;
    private String name;
    private String description;
    private String cityVillage;
    private String stateProvince;
    private String country;
    private String postalCode;
    private String latitude;
    private String longitude;
    private String countryDistrict;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String address6;
    private List<LocationTag> tags;
    private Location parentLocation;
    private List<Location> childLocations;
    private Boolean retired;

    // List<LocationAttribute> attributes

    private List<Link> links;

    private String resourceVersion;

    // region getters
    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCountryDistrict() {
        return countryDistrict;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getAddress4() {
        return address4;
    }

    public String getAddress5() {
        return address5;
    }

    public String getAddress6() {
        return address6;
    }

    public List<LocationTag> getTags() {
        return tags;
    }

    public Location getParentLocation() {
        return parentLocation;
    }

    public List<Location> getChildLocations() {
        return childLocations;
    }

    public Boolean getRetired() {
        return retired;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    // endregion

    // region setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCountryDistrict(String countryDistrict) {
        this.countryDistrict = countryDistrict;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public void setTags(List<LocationTag> tags) {
        this.tags = tags;
    }

    public void setParentLocation(Location parentLocation) {
        this.parentLocation = parentLocation;
    }

    public void setChildLocations(List<Location> childLocations) {
        this.childLocations = childLocations;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    // endregion

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        if (uuid != null) {
            org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(uuid);
            if (omrsLocation != null) {
                return omrsLocation;
            }
        }

        org.openmrs.Location omrsLocation = Context.getLocationService().getLocation(name);
        // If location with given name already exists then we can just return it.
        if (omrsLocation != null) {
            return omrsLocation;
        }
        omrsLocation = new org.openmrs.Location();
        omrsLocation.setUuid(uuid);
        omrsLocation.setName(name);
        omrsLocation.setDescription(description);
        omrsLocation.setCityVillage(cityVillage);
        omrsLocation.setStateProvince(stateProvince);
        omrsLocation.setCountry(country);
        omrsLocation.setPostalCode(postalCode);
        omrsLocation.setLatitude(latitude);
        omrsLocation.setLongitude(longitude);
        omrsLocation.setCountyDistrict(countryDistrict);
        omrsLocation.setAddress1(address1);
        omrsLocation.setAddress2(address2);
        omrsLocation.setAddress3(address3);
        omrsLocation.setAddress4(address4);
        omrsLocation.setAddress5(address5);
        omrsLocation.setAddress6(address6);
        if (tags != null) {
            for (LocationTag locationTag : tags) {
                omrsLocation.addTag((org.openmrs.LocationTag) locationTag.getOpenMrsObject());
            }
        }
        if (parentLocation != null) {
            omrsLocation.setParentLocation((org.openmrs.Location) parentLocation.getOpenMrsObject());
        }
        omrsLocation.setRetired(retired);

        return omrsLocation;
    }

}
