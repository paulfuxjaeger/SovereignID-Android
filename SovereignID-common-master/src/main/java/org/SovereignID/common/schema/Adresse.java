package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Adresse extends Schema {

	private String land;
	private String plz;
	private String ort;
	private String strasse;
	private String hausnummer;
	private String tuer;
	private String adresszusatz;
	private String hauptadresse;

	public Adresse() {
	}

	public Adresse(String issuer, String land, String plz, String ort, String strasse, String hausnummer, String tuer,
			String adresszusatz, String hauptadresse) {
		super(issuer);
		this.land = land;
		this.plz = plz;
		this.ort = ort;
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.tuer = tuer;
		this.adresszusatz = adresszusatz;
		this.hauptadresse = hauptadresse;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("land", this.land);
		claims.put("plz", this.plz);
		claims.put("ort", this.ort);
		claims.put("strasse", this.strasse);
		claims.put("hausnummer", this.hausnummer);
		claims.put("tuer", this.tuer);
		claims.put("adresszusatz", this.adresszusatz);
		claims.put("hauptadresse", this.hauptadresse);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("land".equals(key)) this.land = value;
		if ("plz".equals(key)) this.plz = value;
		if ("ort".equals(key)) this.ort = value;
		if ("strasse".equals(key)) this.strasse = value;
		if ("hausnummer".equals(key)) this.hausnummer = value;
		if ("tuer".equals(key)) this.tuer = value;
		if ("adresszusatz".equals(key)) this.adresszusatz = value;
		if ("hauptadresse".equals(key)) this.hauptadresse = value;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getTuer() {
		return tuer;
	}

	public void setTuer(String tuer) {
		this.tuer = tuer;
	}

	public String getAdresszusatz() {
		return adresszusatz;
	}

	public void setAdresszusatz(String adresszusatz) {
		this.adresszusatz = adresszusatz;
	}

	public String getHauptadresse() {
		return hauptadresse;
	}

	public void setHauptadresse(String hauptadresse) {
		this.hauptadresse = hauptadresse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresszusatz == null) ? 0 : adresszusatz.hashCode());
		result = prime * result + ((hauptadresse == null) ? 0 : hauptadresse.hashCode());
		result = prime * result + ((hausnummer == null) ? 0 : hausnummer.hashCode());
		result = prime * result + ((land == null) ? 0 : land.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
		result = prime * result + ((tuer == null) ? 0 : tuer.hashCode());
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
		Adresse other = (Adresse) obj;
		if (adresszusatz == null) {
			if (other.adresszusatz != null)
				return false;
		} else if (!adresszusatz.equals(other.adresszusatz))
			return false;
		if (hauptadresse == null) {
			if (other.hauptadresse != null)
				return false;
		} else if (!hauptadresse.equals(other.hauptadresse))
			return false;
		if (hausnummer == null) {
			if (other.hausnummer != null)
				return false;
		} else if (!hausnummer.equals(other.hausnummer))
			return false;
		if (land == null) {
			if (other.land != null)
				return false;
		} else if (!land.equals(other.land))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} else if (!strasse.equals(other.strasse))
			return false;
		if (tuer == null) {
			if (other.tuer != null)
				return false;
		} else if (!tuer.equals(other.tuer))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [land=" + land + ", plz=" + plz + ", ort=" + ort + ", strasse=" + strasse + ", hausnummer="
				+ hausnummer + ", tuer=" + tuer + ", adresszusatz=" + adresszusatz + ", hauptadresse=" + hauptadresse
				+ "]";
	}
}
