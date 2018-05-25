package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Visit implements RestResource {

    @Expose
    private String uuid;

    @Expose
    private String display;

    @Expose
    private VisitType visitType;

    @Expose
    private String startDatetime;

    @Expose
    private String stopDatetime;

//    @Expose
//    private Concept indicationConcept;

    @Expose
    private Location location;

    @Expose
    private String creator;

//    @Expose
//    private List<Identifier> identifiers = new ArrayList<Identifier>();
    @Expose
    private Patient patient;

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    @Expose
    private List<Encounter> encounters = new ArrayList<Encounter>();

    public Visit() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }



    /**
     * Converts resource Visit to org.openmrs.Visit
     * @return
     */
    @Override
    public org.openmrs.BaseOpenmrsObject getOpenMrsObject() {
        org.openmrs.Visit visit = new org.openmrs.Visit();
        visit.setUuid(uuid);
        visit.setPatient((org.openmrs.Patient)patient.getOpenMrsObject());
        visit.setVisitType((org.openmrs.VisitType)visitType.getOpenMrsObject());
        try {
            if (getStartDatetime() != null) {
                visit.setStartDatetime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getStartDatetime()));

            } else {
                visit.setStartDatetime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getDisplay().split(" ")[6]));
            }
            if (getStopDatetime() != null) {
                visit.setStopDatetime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getStopDatetime()));
            } else {
                Date stopDate = ((Date) visit.getStartDatetime().clone());
                visit.setStopDatetime(stopDate);
            }
        } catch(ParseException e) {
            e.printStackTrace();
        }
//        visit.setIndication(getIndicationConcept());
        visit.setLocation((org.openmrs.Location)getLocation().getOpenMrsObject());

        Set<org.openmrs.Encounter> encountersSet = new TreeSet<>();
        for(Encounter encounter : encounters) {
            encountersSet.add((org.openmrs.Encounter)(encounter.getOpenMrsObject()));
        }
        visit.setEncounters(encountersSet);
        return visit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Visit visit = (Visit) o;
        return Objects.equals(uuid, visit.uuid) &&
                Objects.equals(display, visit.display) &&
                Objects.equals(startDatetime, visit.startDatetime) &&
                Objects.equals(stopDatetime, visit.stopDatetime) &&
//                Objects.equals(indicationConcept, visit.indicationConcept) &&
                Objects.equals(location, visit.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, startDatetime, stopDatetime,  location);
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getStopDatetime() {
        return stopDatetime;
    }

    public void setStopDatetime(String stopDatetime) {
        this.stopDatetime = stopDatetime;
    }

//    public Concept getIndicationConcept() {
//        return indicationConcept;
//    }

//    public void setIndicationConcept(Concept indicationConcept) {
//        this.indicationConcept = indicationConcept;
//    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = new Patient();
        this.patient.setUuid(patient.getPerson().getUuid());
    }

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

}
