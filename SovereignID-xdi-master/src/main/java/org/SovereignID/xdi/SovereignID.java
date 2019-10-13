package org.SovereignID.xdi;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.SovereignID.common.DIDXDI;
import org.SovereignID.common.schema.Schema;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class SovereignIDXDI {

	public static final XDIAddress XDI_ADD_ISSUER = XDIAddress.create("#issuer");
	public static final XDIAddress XDI_ADD_SCHEMA = XDIAddress.create("#schema");
	public static final XDIAddress XDI_ADD_IS_SCHEMA = XDIAddress.create("$is#schema");

	public static void route(org.SovereignID.common.message.Message message) {

		System.out.println("route: " + message);

		try {

			// route

			HttpURLConnection conn = (HttpURLConnection) new URL("https://raiffeisen.SovereignID.danubetech.com/message").openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			writer.println(message.toJson());
			writer.flush();

			if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

			// XDI

			String from = message.getFrom();
			String to = message.getTo();
			URI endpoint = DIDXDI.xdiEndpoint(from);

			if (endpoint != null) {

				for (Schema schema : message.getSchemas()) SovereignIDXDI.writeSchema(from, to, schema, endpoint);
				for (Schema schema : message.getSenderschemas()) SovereignIDXDI.writeSchema(from, to, schema, endpoint);
			}
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static void writeSchema(String sender, String did, Schema schema, URI endpoint) throws IOException {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		XDIAddress schemaXDIaddress = XDIAddressUtil.concatXDIAddresses(
				XDIAddress.create(did),
				XDIAddress.fromComponent(XdiEntityInstanceUnordered.createXDIArc(schema.getId())));

		for (Entry<String, String> claim : schema.getClaims().entrySet()) {

			XDIAddress claimXDIaddress = XDIAddressUtil.concatXDIAddresses(
					schemaXDIaddress,
					XDIAddress.create("<#" + claim.getKey() + ">"));

			graph.setStatement(XDIStatement.fromLiteralComponents(
					claimXDIaddress, 
					claim.getValue()));
		}

		if (schema.getIssuer() != null) {

			graph.setStatement(XDIStatement.fromRelationComponents(
					schemaXDIaddress, 
					XDI_ADD_ISSUER, 
					XDIAddress.create(schema.getIssuer())));
		}

		if (schema.getSignature() != null) {

			XDIAddress signatureXDIaddress = XDIAddressUtil.concatXDIAddresses(schemaXDIaddress, XDIAddress.create("<$sig>"));

			graph.setStatement(XDIStatement.fromLiteralComponents(
					signatureXDIaddress, 
					schema.getSignature()));
		}

		graph.setStatement(XDIStatement.fromRelationComponents(
				schemaXDIaddress, 
				XDI_ADD_IS_SCHEMA, 
				XDIAddress.create("#" + Schema.getName(schema))));

		graph.setStatement(XDIStatement.fromRelationComponents(
				schemaXDIaddress, 
				XDIDictionaryConstants.XDI_ADD_IS_TYPE, 
				XDI_ADD_SCHEMA));

		// write to sender graph

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(sender), -1);
		m1.setFromXDIAddress(XDIAddress.create(sender));
		m1.setToXDIAddress(XDIAddress.create(sender));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createDelOperation(schemaXDIaddress);
		Message m2 = me1.createMessage(XDIAddress.create(sender), -1);
		m2.setFromXDIAddress(XDIAddress.create(sender));
		m2.setToXDIAddress(XDIAddress.create(sender));
		m2.setLinkContractClass(RootLinkContract.class);
		m2.createSetOperation(graph);

		XDIHttpClient client1 = new XDIHttpClient(endpoint);

		try {

			client1.send(me1);
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}
	}

	public static Map<String, List<Schema>> readSchemas(String sender, URI endpoint) throws IOException {

		// read from sender graph

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(sender), -1);
		m1.setFromXDIAddress(XDIAddress.create(sender));
		m1.setToXDIAddress(XDIAddress.create(sender));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createGetOperation(XDIConstants.XDI_ADD_ROOT);

		XDIHttpClient client1 = new XDIHttpClient(endpoint);
		Graph resultGraph;

		try {

			resultGraph = client1.send(me1).getResultGraph();
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}

		// read schemas

		Map<String, List<Schema>> schemaMap = new HashMap<String, List<Schema>> ();

		for (ContextNode contextNode : resultGraph.getRootContextNode().getAllContextNodes()) {

			if (! contextNode.containsRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, XDI_ADD_SCHEMA)) continue;

			String did = contextNode.getXDIAddress().getFirstXDIArc().toString();
			List<Schema> schemaList = schemaMap.get(did);
			if (schemaList == null) { schemaList = new ArrayList<Schema> (); schemaMap.put(did, schemaList); }

			String schemaName = contextNode.getRelation(XDI_ADD_IS_SCHEMA).getTargetXDIAddress().getFirstXDIArc().getLiteral();
			String schemaId = contextNode.getXDIArc().getLiteral();

			Schema schema = Schema.emptyForName(schemaName); schema.setId(schemaId);
			for (ContextNode claimContextNode : contextNode.getContextNodes()) {

				String claimKey = claimContextNode.getXDIArc().getLiteral();
				String claimValue = claimContextNode.getLiteralDataString();
				schema.setClaim(claimKey, claimValue);
			}

			Relation issuerRelation = contextNode.getRelation(XDI_ADD_ISSUER);
			String issuer = issuerRelation == null ? null : issuerRelation.getTargetXDIAddress().toString();
			if (issuer != null) schema.setIssuer(issuer);

			LiteralNode signatureLiteralNode = contextNode.getDeepLiteralNode(XDIAddress.create("<$sig>"));
			String signature = signatureLiteralNode == null ? null : signatureLiteralNode.getLiteralDataString();
			if (signature != null) schema.setSignature(signature);

			schemaList.add(schema);
		}

		return schemaMap;
	}

	public static Map<String, List<Schema>> readSchemas(String sender, URI endpoint, String address) throws IOException {

		// read from sender graph

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(sender), -1);
		m1.setFromXDIAddress(XDIAddress.create(sender));
		m1.setToXDIAddress(XDIAddress.create(sender));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createGetOperation(XDIAddress.create(address));

		XDIHttpClient client1 = new XDIHttpClient(endpoint);
		Graph resultGraph;

		try {

			resultGraph = client1.send(me1).getResultGraph();
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}

		// read schemas

		Map<String, List<Schema>> schemaMap = new HashMap<String, List<Schema>> ();

		ContextNode addressContextNode = resultGraph.getDeepContextNode(XDIAddress.create(address));
		if (addressContextNode != null) for (ContextNode contextNode : addressContextNode.getAllContextNodes()) {

			if (! contextNode.containsRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, XDI_ADD_SCHEMA)) continue;

			String did = contextNode.getXDIAddress().getFirstXDIArc().toString();
			List<Schema> schemaList = schemaMap.get(did);
			if (schemaList == null) { schemaList = new ArrayList<Schema> (); schemaMap.put(did, schemaList); }

			String schemaName = contextNode.getRelation(XDI_ADD_IS_SCHEMA).getTargetXDIAddress().getFirstXDIArc().getLiteral();
			String schemaId = contextNode.getXDIArc().getLiteral();

			Schema schema = Schema.emptyForName(schemaName); schema.setId(schemaId);
			for (ContextNode claimContextNode : contextNode.getContextNodes()) {

				String claimKey = claimContextNode.getXDIArc().getLiteral();
				String claimValue = claimContextNode.getLiteralDataString();
				schema.setClaim(claimKey, claimValue);
			}

			Relation issuerRelation = contextNode.getRelation(XDI_ADD_ISSUER);
			String issuer = issuerRelation == null ? null : issuerRelation.getTargetXDIAddress().toString();
			if (issuer != null) schema.setIssuer(issuer);

			schemaList.add(schema);
		}

		return schemaMap;
	}

	public static <S extends Schema> S readSchema(String sender, URI endpoint, String address, Class<S> cl) throws IOException {

		Map<String, List<Schema>> schemaMap = readSchemas(sender, endpoint, address);

		for (List<Schema> schemaList : schemaMap.values()) {

			for (Schema schema : schemaList) {

				if (cl.isAssignableFrom(schema.getClass())) return (S) schema;
			}
		}

		return null;
	}

	public static Schema readSchema(String sender, URI endpoint, String address) throws IOException {

		// read from sender graph

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(sender), -1);
		m1.setFromXDIAddress(XDIAddress.create(sender));
		m1.setToXDIAddress(XDIAddress.create(sender));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createGetOperation(XDIAddress.create(address));

		XDIHttpClient client1 = new XDIHttpClient(endpoint);
		Graph resultGraph;

		try {

			resultGraph = client1.send(me1).getResultGraph();
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}

		// read schema

		Schema schema = null;

		ContextNode addressContextNode = resultGraph.getDeepContextNode(XDIAddress.create(address));
		if (addressContextNode != null) {

			if (! addressContextNode.containsRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, XDI_ADD_SCHEMA)) return null;

			String did = addressContextNode.getXDIAddress().getFirstXDIArc().toString();

			String schemaName = addressContextNode.getRelation(XDI_ADD_IS_SCHEMA).getTargetXDIAddress().getFirstXDIArc().getLiteral();
			String schemaId = addressContextNode.getXDIArc().getLiteral();

			schema = Schema.emptyForName(schemaName); schema.setId(schemaId);
			for (ContextNode claimContextNode : addressContextNode.getContextNodes()) {

				String claimKey = claimContextNode.getXDIArc().getLiteral();
				String claimValue = claimContextNode.getLiteralDataString();
				schema.setClaim(claimKey, claimValue);
			}

			Relation issuerRelation = addressContextNode.getRelation(XDI_ADD_ISSUER);
			String issuer = issuerRelation == null ? null : issuerRelation.getTargetXDIAddress().toString();
			if (issuer != null) schema.setIssuer(issuer);
		}

		return schema;
	}

	public static void updateClaim(String sender, URI endpoint, String address, String schemaName, String claimsKey, String value) throws IOException {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		XDIAddress claimXDIaddress = XDIAddressUtil.concatXDIAddresses(
				XDIAddress.create(address),
				XDIAddress.create("<#" + claimsKey + ">"));

		graph.setStatement(XDIStatement.fromLiteralComponents(
				claimXDIaddress, 
				value));

		// write to sender graph

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(sender), -1);
		m1.setFromXDIAddress(XDIAddress.create(sender));
		m1.setToXDIAddress(XDIAddress.create(sender));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createSetOperation(graph);

		XDIHttpClient client1 = new XDIHttpClient(endpoint);

		try {

			client1.send(me1).getResultGraph();
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}
	}

	public static void makeAgent(String agency, String did) throws IOException {

		XDIAddress XDIaddress = XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(XDIAddress.create(did)));
		XDIStatement XDIstatement = XDIStatement.create(XDIaddress.toString() + did + "/$ref/" + XDIaddress.toString());

		MessageEnvelope me1 = new MessageEnvelope();
		Message m1 = me1.createMessage(XDIAddress.create(agency));
		m1.setFromXDIAddress(XDIAddress.create(agency));
		m1.setToXDIAddress(XDIAddress.create(agency));
		m1.setLinkContractClass(RootLinkContract.class);
		m1.createSetOperation(XDIstatement);

		XDIHttpClient client1 = new XDIHttpClient(DIDXDI.xdiEndpoint(agency));

		try {

			client1.send(me1);
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client1.close();
		}

		MessageEnvelope me2 = new MessageEnvelope();
		Message m2 = me2.createMessage(XDIAddress.create(agency));
		m2.setFromXDIAddress(XDIAddress.create(agency));
		m2.setToXDIAddress(XDIAddress.create(did));
		m2.setLinkContractClass(RootLinkContract.class);
		m2.createSetOperation(XDIstatement);

		XDIHttpClient client2 = new XDIHttpClient(DIDXDI.userXdiEndpoint(agency, did));

		try {

			client2.send(me2);
		} catch (Xdi2ClientException ex) {

			throw new IOException(ex.getMessage(), ex);
		} finally {

			client2.close();
		}
	}
}
