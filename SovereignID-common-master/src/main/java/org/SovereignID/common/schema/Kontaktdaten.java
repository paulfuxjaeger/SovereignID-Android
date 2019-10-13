package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Kontaktdaten extends Schema {

	private String mobiltel;
	private String emailadresse;
	private String skypeadresse;

	public Kontaktdaten() {
	}
	public Kontaktdaten(String issuer, String mobiltel, String emailadresse, String skypeadresse) {
		super(issuer);
		this.mobiltel = mobiltel;
		this.emailadresse = emailadresse;
		this.skypeadresse = skypeadresse;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("mobiltel", this.mobiltel);
		claims.put("emailadresse", this.emailadresse);
		claims.put("skypeadresse", this.skypeadresse);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("mobiltel".equals(key)) this.mobiltel = value;
		if ("emailadresse".equals(key)) this.emailadresse = value;
		if ("skypeadresse".equals(key)) this.skypeadresse = value;
	}

	public String getMobiltel() {
		return mobiltel;
	}

	public void setMobiltel(String mobiltel) {
		this.mobiltel = mobiltel;
	}

	public String getEmailadresse() {
		return emailadresse;
	}

	public void setEmailadresse(String emailadresse) {
		this.emailadresse = emailadresse;
	}

	public String getSkypeadresse() {
		return skypeadresse;
	}

	public void setSkypeadresse(String skypeadresse) {
		this.skypeadresse = skypeadresse;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailadresse == null) ? 0 : emailadresse.hashCode());
		result = prime * result + ((mobiltel == null) ? 0 : mobiltel.hashCode());
		result = prime * result + ((skypeadresse == null) ? 0 : skypeadresse.hashCode());
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
		Kontaktdaten other = (Kontaktdaten) obj;
		if (emailadresse == null) {
			if (other.emailadresse != null)
				return false;
		} else if (!emailadresse.equals(other.emailadresse))
			return false;
		if (mobiltel == null) {
			if (other.mobiltel != null)
				return false;
		} else if (!mobiltel.equals(other.mobiltel))
			return false;
		if (skypeadresse == null) {
			if (other.skypeadresse != null)
				return false;
		} else if (!skypeadresse.equals(other.skypeadresse))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Kontaktdaten [mobiltel=" + mobiltel + ", emailadresse=" + emailadresse + ", skypeadresse="
				+ skypeadresse + "]";
	}
}
