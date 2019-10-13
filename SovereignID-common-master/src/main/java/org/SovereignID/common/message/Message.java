package org.SovereignID.common.message;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.SovereignID.common.schema.Schema;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	static {

		objectMapper.enableDefaultTyping();
	}

	private String from;
	private String to;
	private String type;
	private List<Schema> schemas;
	private List<Schema> senderschemas;

	public static Message fromJson(String json) {

		try {

			return (Message) objectMapper.readValue(json, Message.class);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static Message fromJson(Reader reader) {

		try {

			return (Message) objectMapper.readValue(reader, Message.class);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public String toJson() {

		try {

			return objectMapper.writeValueAsString(this);
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public Message() {
		this.schemas = new ArrayList<Schema> ();
		this.senderschemas = new ArrayList<Schema> ();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	public List<Schema> getSenderschemas() {
		return senderschemas;
	}

	public void setSenderschemas(List<Schema> senderschemas) {
		this.senderschemas = senderschemas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((schemas == null) ? 0 : schemas.hashCode());
		result = prime * result + ((senderschemas == null) ? 0 : senderschemas.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Message other = (Message) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (schemas == null) {
			if (other.schemas != null)
				return false;
		} else if (!schemas.equals(other.schemas))
			return false;
		if (senderschemas == null) {
			if (other.senderschemas != null)
				return false;
		} else if (!senderschemas.equals(other.senderschemas))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.toJson();
	}
}
