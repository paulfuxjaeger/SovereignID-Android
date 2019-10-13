package org.SovereignID.common.event;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.SovereignID.common.schema.Schema;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SchemaEvent implements Comparable<SchemaEvent> {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	public static final String EVENT_DID = "DID";
	public static final String EVENT_OFFER = "OFFER";
	public static final String EVENT_REQUEST = "REQUEST";
	public static final String EVENT_AGENT = "AGENT";

	static {

		objectMapper.enableDefaultTyping();
	}

	private long time;
	private String event;
	private String did;
	private String schema;
	private String text;

	public SchemaEvent(long time, String event, String did, String schema, String text) {

		this.time = time;
		this.event = event;
		this.did = did;
		this.schema = schema;
		this.text = text;
	}

	public SchemaEvent(String event, String did, String schema, String text) {

		this(System.currentTimeMillis(), event, did, schema, text);
	}

	public SchemaEvent(String event, String did, Schema schema, String text) {

		this(System.currentTimeMillis(), event, did, Schema.getName(schema), text);
	}

	public SchemaEvent(String event, Schema schema, String text) {

		this(System.currentTimeMillis(), event, schema.getIssuer(), Schema.getName(schema), text);
	}

	public SchemaEvent(String event, String did, String schema) {

		this(System.currentTimeMillis(), event, did, schema, null);
	}

	public SchemaEvent(String event, String did, Schema schema) {

		this(System.currentTimeMillis(), event, did, Schema.getName(schema), null);
	}

	public SchemaEvent(String event, Schema schema) {

		this(System.currentTimeMillis(), event, schema.getIssuer(), Schema.getName(schema), null);
	}

	public static SchemaEvent fromJson(String json) {

		try {

			return (SchemaEvent) objectMapper.readValue(json, ArrayList.class).get(0);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static SchemaEvent fromJson(Reader reader) {

		try {

			return (SchemaEvent) objectMapper.readValue(reader, ArrayList.class).get(0);
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

	public SchemaEvent() {
		super();
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "SchemaEvent [time=" + time + ", event=" + event + ", did=" + did + ", schema=" + schema + ", text="
				+ text + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((did == null) ? 0 : did.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
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
		SchemaEvent other = (SchemaEvent) obj;
		if (did == null) {
			if (other.did != null)
				return false;
		} else if (!did.equals(other.did))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (time != other.time)
			return false;
		return true;
	}

	@Override
	public int compareTo(SchemaEvent other) {

		return Long.compare(this.time, other.time);
	}
}
