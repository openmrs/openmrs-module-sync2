package org.openmrs.module.sync2.person;

import java.util.Calendar;
import java.util.Date;


public class PersonObject {
	
	private String uuid;
	
	private Date birthDate;
	
	private String gender;
	
	private Integer age;
	
	private String display;
	
	public PersonObject() {
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String grtUuid() {
		return this.uuid;
	}
	
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public Date getDate() {
		return this.birthDate;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getGender() {
		return this.gender;
	}
	
	public Integer getAge(Date onDate) {
		if (birthDate == null) {
			return null;
		}
		
		// Use default end date as today.
		Calendar today = Calendar.getInstance();
		// But if given, use the given date.
		if (onDate != null) {
			today.setTime(onDate);
		}
		
		Calendar bday = Calendar.getInstance();
		bday.setTime(birthDate);
		
		this.age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);
		
		// Adjust age when today's date is before the person's birthday
		int todaysMonth = today.get(Calendar.MONTH);
		int bdayMonth = bday.get(Calendar.MONTH);
		int todaysDay = today.get(Calendar.DAY_OF_MONTH);
		int bdayDay = bday.get(Calendar.DAY_OF_MONTH);
		
		if (todaysMonth < bdayMonth) {
			this.age--;
		} else if (todaysMonth == bdayMonth && todaysDay < bdayDay) {
			// we're only comparing on month and day, not minutes, etc
			this.age--;
		}
		
		return this.age;
	}
	
	public void SetDisplay(String display) {
		this.display = display;
	}
	
	public String getDisplay(String display) {
		return this.display;
	}

}