package org.SovereignID.common.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Schema {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	static {

		objectMapper.enableDefaultTyping();
	}

	private String id;
	private String issuer;

	public static Schema fromJson(String json) {

		try {

			return (Schema) objectMapper.readValue(json, ArrayList.class).get(0);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static Schema fromJson(Reader reader) {

		try {

			return (Schema) objectMapper.readValue(reader, ArrayList.class).get(0);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public String toJson() {

		ArrayList list = new ArrayList();
		list.add(this);

		try {

			return objectMapper.writeValueAsString(list);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public Schema() {
		super();
		this.id = UUID.randomUUID().toString();
		this.issuer = null;
	}

	public Schema(String issuer) {
		super();
		this.id = UUID.randomUUID().toString();
		this.issuer = issuer;
	}

	public static String getName(Schema schema) {

		return schema.getClass().getSimpleName().toLowerCase();
	}

	public abstract LinkedHashMap<String, String> getClaims(); 

	public abstract void setClaim(String key, String value);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
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
		Schema other = (Schema) obj;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.toJson();
	}

	public static Schema emptyForName(String name) {

		Schema schema = null;

		if ("adresse".equals(name.toLowerCase())) schema = new Adresse();
		if ("bankdaten".equals(name.toLowerCase())) schema = new Bankdaten();
		if ("basisdaten".equals(name.toLowerCase())) schema = new Basisdaten();
		if ("jobdaten".equals(name.toLowerCase())) schema = new Jobdaten();
		if ("kontaktdaten".equals(name.toLowerCase())) schema = new Kontaktdaten();
		if ("versicherung".equals(name.toLowerCase())) schema = new Versicherung();

		if (schema == null) throw new IllegalArgumentException();

		return schema;
	}

	public static Schema templateForName(String name) {

		Schema schema = emptyForName(name);
		schema.id = null;

		return schema;
	}
}
