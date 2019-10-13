package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Basisdaten extends Schema {

	private String vorname;
	private String nachname;
	private String geburtsdatum;
	private String staatsbuergerschaft;
	private String geschlecht;

	public Basisdaten() {
	}
	public Basisdaten(String issuer, String vorname, String nachname, String geburtsdatum, String staatsbuergerschaft,
			String geschlecht) {
		super(issuer);
		this.vorname = vorname;
		this.nachname = nachname;
		this.geburtsdatum = geburtsdatum;
		this.staatsbuergerschaft = staatsbuergerschaft;
		this.geschlecht = geschlecht;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("vorname", this.vorname);
		claims.put("nachname", this.nachname);
		claims.put("geburtsdatum", this.geburtsdatum);
		claims.put("staatsbuergerschaft", this.staatsbuergerschaft);
		claims.put("geschlecht", this.geschlecht);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("vorname".equals(key)) this.vorname = value;
		if ("nachname".equals(key)) this.nachname = value;
		if ("geburtsdatum".equals(key)) this.geburtsdatum = value;
		if ("staatsbuergerschaft".equals(key)) this.staatsbuergerschaft = value;
		if ("geschlecht".equals(key)) this.geschlecht = value;
	}

	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	public String getGeburtsdatum() {
		return geburtsdatum;
	}
	public void setGeburtsdatum(String geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}
	public String getStaatsbuergerschaft() {
		return staatsbuergerschaft;
	}
	public void setStaatsbuergerschaft(String staatsbuergerschaft) {
		this.staatsbuergerschaft = staatsbuergerschaft;
	}
	public String getGeschlecht() {
		return geschlecht;
	}
	public void setGeschlecht(String geschlecht) {
		this.geschlecht = geschlecht;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geburtsdatum == null) ? 0 : geburtsdatum.hashCode());
		result = prime * result + ((geschlecht == null) ? 0 : geschlecht.hashCode());
		result = prime * result + ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result + ((staatsbuergerschaft == null) ? 0 : staatsbuergerschaft.hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
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
		Basisdaten other = (Basisdaten) obj;
		if (geburtsdatum == null) {
			if (other.geburtsdatum != null)
				return false;
		} else if (!geburtsdatum.equals(other.geburtsdatum))
			return false;
		if (geschlecht == null) {
			if (other.geschlecht != null)
				return false;
		} else if (!geschlecht.equals(other.geschlecht))
			return false;
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		} else if (!nachname.equals(other.nachname))
			return false;
		if (staatsbuergerschaft == null) {
			if (other.staatsbuergerschaft != null)
				return false;
		} else if (!staatsbuergerschaft.equals(other.staatsbuergerschaft))
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		} else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Basisdaten [vorname=" + vorname + ", nachname=" + nachname + ", geburtsdatum=" + geburtsdatum
				+ ", staatsbuergerschaft=" + staatsbuergerschaft + ", geschlecht=" + geschlecht + "]";
	}
}
