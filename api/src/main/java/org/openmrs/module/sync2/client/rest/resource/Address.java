package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.PersonAddress;

import java.util.Date;
import java.util.Objects;

public class Address implements RestResource {

    private String uuid;

    @Expose
    private String address1;
    @Expose
    private String address2;
    @Expose
    private String cityVillage;
    @Expose
    private String stateProvince;
    @Expose
    private String country;
    @Expose
    private String postalCode;
    @Expose
    private String countyDistrict;
    @Expose
    private String address3;
    @Expose
    private String address4;
    @Expose
    private String address5;
    @Expose
    private String address6;
    @Expose
    private Date startDate;
    @Expose
    private Date endDate;
    @Expose
    private String latitude;
    @Expose
    private String longitude;

    public Address(Builder builder) {
        this.uuid = builder.uuid;
        this.address1 = builder.address1;
        this.address2 = builder.address2;
        this.address3 = builder.address3;
        this.address4 = builder.address4;
        this.address5 = builder.address5;
        this.address6 = builder.address6;
        this.cityVillage = builder.cityVillage;
        this.stateProvince = builder.stateProvince;
        this.country = builder.country;
        this.postalCode = builder.postalCode;
        this.countyDistrict = builder.countyDistrict;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
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

    public String getCountyDistrict() {
        return countyDistrict;
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFullAddressString() {
        StringBuilder address = new StringBuilder();
        return  address.append(address1).append(",").append(address2).append(",")
                .append(cityVillage).append(",").append(stateProvince).append(",")
                .append(country).append(",").append(postalCode).append(",")
                .append(countyDistrict).append(",").append(address3).append(",")
                .append(address4).append(",").append(address5).append(",")
                .append(address6).append(",").append(startDate).append(",")
                .append(endDate).append(",").append(latitude).append(",")
                .append(longitude)
                .toString();
    }

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        PersonAddress address = new PersonAddress();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setAddress3(address3);
        address.setAddress4(address4);
        address.setAddress5(address5);
        address.setAddress6(address6);
        address.setStartDate(startDate);
        address.setEndDate(endDate);
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setCountyDistrict(countyDistrict);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setStateProvince(stateProvince);
        address.setCityVillage(cityVillage);
        address.setUuid(uuid);
        return address;
    }

    public static class Builder {
        private String uuid;
        private String address1;
        private String address2;
        private String cityVillage;
        private String stateProvince;
        private String country;
        private String postalCode;
        private String countyDistrict;
        private String address3;
        private String address4;
        private String address5;
        private String address6;
        private Date startDate;
        private Date endDate;
        private String latitude;
        private String longitude;

        public Builder() {
        }

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setAddress1(String address1) {
            this.address1 = address1;
            return this;
        }

        public Builder setAddress2(String address2) {
            this.address2 = address2;
            return this;
        }

        public Builder setCityVillage(String cityVillage) {
            this.cityVillage = cityVillage;
            return this;
        }

        public Builder setStateProvince(String stateProvince) {
            this.stateProvince = stateProvince;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder setCountyDistrict(String countyDistrict) {
            this.countyDistrict = countyDistrict;
            return this;
        }

        public Builder setAddress3(String address3) {
            this.address3 = address3;
            return this;
        }

        public Builder setAddress4(String address4) {
            this.address4 = address4;
            return this;
        }

        public Builder setAddress5(String address5) {
            this.address5 = address5;
            return this;
        }

        public Builder setAddress6(String address6) {
            this.address6 = address6;
            return this;
        }

        public Builder setStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public Address create() {
            return new Address(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;
        return Objects.equals(uuid, address.uuid) &&
                Objects.equals(address1, address.address1) &&
                Objects.equals(address2, address.address2) &&
                Objects.equals(cityVillage, address.cityVillage) &&
                Objects.equals(stateProvince, address.stateProvince) &&
                Objects.equals(country, address.country) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(countyDistrict, address.countyDistrict) &&
                Objects.equals(address3, address.address3) &&
                Objects.equals(address4, address.address4) &&
                Objects.equals(address5, address.address5) &&
                Objects.equals(address6, address.address6) &&
                Objects.equals(startDate, address.startDate) &&
                Objects.equals(endDate, address.endDate) &&
                Objects.equals(latitude, address.latitude) &&
                Objects.equals(longitude, address.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, address1, address2, cityVillage, stateProvince, country, postalCode, countyDistrict, address3, address4, address5, address6, startDate, endDate, latitude, longitude);
    }
}
