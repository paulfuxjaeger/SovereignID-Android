package org.SovereignID.common.schema;

import java.util.LinkedHashMap;

public class Versicherung extends Schema {

	private String art;
	private String deckung;
	private String gueltigkeit;

	public Versicherung() {
	}

	public Versicherung(String issuer, String art, String deckung, String gueltigkeit) {
		super(issuer);
		this.art = art;
		this.deckung = deckung;
		this.gueltigkeit = gueltigkeit;
	}

	@Override
	public LinkedHashMap<String, String> getClaims() {

		LinkedHashMap<String, String> claims = new LinkedHashMap<String, String> ();
		claims.put("art", this.art);
		claims.put("deckung", this.deckung);
		claims.put("gueltigkeit", this.gueltigkeit);

		return claims;
	}

	@Override
	public void setClaim(String key, String value) {

		if ("art".equals(key)) this.art = value;
		if ("deckung".equals(key)) this.deckung = value;
		if ("gueltigkeit".equals(key)) this.gueltigkeit = value;
	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getDeckung() {
		return deckung;
	}

	public void setDeckung(String deckung) {
		this.deckung = deckung;
	}

	public String getGueltigkeit() {
		return gueltigkeit;
	}

	public void setGueltigkeit(String gueltigkeit) {
		this.gueltigkeit = gueltigkeit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((art == null) ? 0 : art.hashCode());
		result = prime * result + ((deckung == null) ? 0 : deckung.hashCode());
		result = prime * result + ((gueltigkeit == null) ? 0 : gueltigkeit.hashCode());
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
		Versicherung other = (Versicherung) obj;
		if (art == null) {
			if (other.art != null)
				return false;
		} else if (!art.equals(other.art))
			return false;
		if (deckung == null) {
			if (other.deckung != null)
				return false;
		} else if (!deckung.equals(other.deckung))
			return false;
		if (gueltigkeit == null) {
			if (other.gueltigkeit != null)
				return false;
		} else if (!gueltigkeit.equals(other.gueltigkeit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Versicherung [art=" + art + ", deckung=" + deckung + ", gueltigkeit=" + gueltigkeit + "]";
	}

}
