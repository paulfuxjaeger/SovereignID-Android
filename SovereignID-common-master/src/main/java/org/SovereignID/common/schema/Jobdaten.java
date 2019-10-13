package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Jobdaten extends Schema {

	private String firmenname;
	private String email;
	private String jobtitel;
	private String berechtigung;
	private String rahmen;

	public Jobdaten() {
	}

	public Jobdaten(String issuer, String firmenname, String email, String jobtitel, String berechtigung, String rahmen) {
		super(issuer);
		this.firmenname = firmenname;
		this.email = email;
		this.jobtitel = jobtitel;
		this.berechtigung = berechtigung;
		this.rahmen = rahmen;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("firmenname", this.firmenname);
		claims.put("email", this.email);
		claims.put("jobtitel", this.jobtitel);
		claims.put("berechtigung", this.berechtigung);
		claims.put("rahmen", this.rahmen);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("firmenname".equals(key)) this.firmenname = value;
		if ("email".equals(key)) this.email = value;
		if ("jobtitel".equals(key)) this.jobtitel = value;
		if ("berechtigung".equals(key)) this.berechtigung = value;
		if ("rahmen".equals(key)) this.rahmen = value;
	}

	public String getFirmenname() {
		return firmenname;
	}

	public void setFirmenname(String firmenname) {
		this.firmenname = firmenname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJobtitel() {
		return jobtitel;
	}

	public void setJobtitel(String jobtitel) {
		this.jobtitel = jobtitel;
	}

	public String getBerechtigung() {
		return berechtigung;
	}

	public void setBerechtigung(String berechtigung) {
		this.berechtigung = berechtigung;
	}

	public String getRahmen() {
		return rahmen;
	}

	public void setRahmen(String rahmen) {
		this.rahmen = rahmen;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((berechtigung == null) ? 0 : berechtigung.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firmenname == null) ? 0 : firmenname.hashCode());
		result = prime * result + ((jobtitel == null) ? 0 : jobtitel.hashCode());
		result = prime * result + ((rahmen == null) ? 0 : rahmen.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Jobdaten other = (Jobdaten) obj;
		if (berechtigung == null) {
			if (other.berechtigung != null)
				return false;
		} else if (!berechtigung.equals(other.berechtigung))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firmenname == null) {
			if (other.firmenname != null)
				return false;
		} else if (!firmenname.equals(other.firmenname))
			return false;
		if (jobtitel == null) {
			if (other.jobtitel != null)
				return false;
		} else if (!jobtitel.equals(other.jobtitel))
			return false;
		if (rahmen == null) {
			if (other.rahmen != null)
				return false;
		} else if (!rahmen.equals(other.rahmen))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Jobdaten [firmenname=" + firmenname + ", email=" + email + ", jobtitel=" + jobtitel + ", berechtigung="
				+ berechtigung + ", rahmen=" + rahmen + "]";
	}
}
