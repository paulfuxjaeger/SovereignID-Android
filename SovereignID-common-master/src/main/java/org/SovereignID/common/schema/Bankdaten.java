package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Bankdaten extends Schema {

	private String iban;
	private String bic;
	private String kontoinhaber;
	private String zeichnungsberechtigter;
	private String bonitaet;

	public Bankdaten() {
	}

	public Bankdaten(String issuer, String iban, String bic, String kontoinhaber, String zeichnungsberechtigter, String bonitaet) {
		super(issuer);
		this.iban = iban;
		this.bic = bic;
		this.kontoinhaber = kontoinhaber;
		this.zeichnungsberechtigter = zeichnungsberechtigter;
		this.bonitaet = bonitaet;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("iban", this.iban);
		claims.put("bic", this.bic);
		claims.put("kontoinhaber", this.kontoinhaber);
		claims.put("zeichnungsberechtigter", this.zeichnungsberechtigter);
		claims.put("bonitaet", this.bonitaet);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("iban".equals(key)) this.iban = value;
		if ("bic".equals(key)) this.bic = value;
		if ("kontoinhaber".equals(key)) this.kontoinhaber = value;
		if ("zeichnungsberechtigter".equals(key)) this.zeichnungsberechtigter = value;
		if ("bonitaet".equals(key)) this.bonitaet = value;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getKontoinhaber() {
		return kontoinhaber;
	}

	public void setKontoinhaber(String kontoinhaber) {
		this.kontoinhaber = kontoinhaber;
	}

	public String getZeichnungsberechtigter() {
		return zeichnungsberechtigter;
	}

	public void setZeichnungsberechtigter(String zeichnungsberechtigter) {
		this.zeichnungsberechtigter = zeichnungsberechtigter;
	}

	public String getBonitaet() {
		return bonitaet;
	}

	public void setBonitaet(String bonitaet) {
		this.bonitaet = bonitaet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bic == null) ? 0 : bic.hashCode());
		result = prime * result + ((bonitaet == null) ? 0 : bonitaet.hashCode());
		result = prime * result + ((iban == null) ? 0 : iban.hashCode());
		result = prime * result + ((kontoinhaber == null) ? 0 : kontoinhaber.hashCode());
		result = prime * result + ((zeichnungsberechtigter == null) ? 0 : zeichnungsberechtigter.hashCode());
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
		Bankdaten other = (Bankdaten) obj;
		if (bic == null) {
			if (other.bic != null)
				return false;
		} else if (!bic.equals(other.bic))
			return false;
		if (bonitaet == null) {
			if (other.bonitaet != null)
				return false;
		} else if (!bonitaet.equals(other.bonitaet))
			return false;
		if (iban == null) {
			if (other.iban != null)
				return false;
		} else if (!iban.equals(other.iban))
			return false;
		if (kontoinhaber == null) {
			if (other.kontoinhaber != null)
				return false;
		} else if (!kontoinhaber.equals(other.kontoinhaber))
			return false;
		if (zeichnungsberechtigter == null) {
			if (other.zeichnungsberechtigter != null)
				return false;
		} else if (!zeichnungsberechtigter.equals(other.zeichnungsberechtigter))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bankdaten [iban=" + iban + ", bic=" + bic + ", kontoinhaber=" + kontoinhaber
				+ ", zeichnungsberechtigter=" + zeichnungsberechtigter + ", bonitaet=" + bonitaet + "]";
	}

}
