package org.SovereignID.common;

import java.net.URI;

/**
 * Created by markus on 3/20/18.
 */

public class DIDXDI {

	public static URI xdiEndpoint(String did) {

		if (did == null) throw new NullPointerException();

		System.out.println("xdiEndpoint(" + did + ")");
		
		if (DIDs.DID_NTB.equals(did))  return URI.create("http://ntb.SovereignID.danubetech.com:7071/xdi/graph");
		if (DIDs.DID_RAIFFEISEN.equals(did))  return URI.create("http://raiffeisen.SovereignID.danubetech.com:7072/xdi/graph");
		if (DIDs.DID_UNIQA.equals(did))  return URI.create("http://uniqa.SovereignID.danubetech.com:7073/xdi/graph");
		if (DIDs.DID_POST.equals(did))  return URI.create("http://post.SovereignID.danubetech.com:7074/xdi/graph");
		if (DIDs.DID_SOLLKAUFEN.equals(did))  return URI.create("http://sollkaufen.SovereignID.danubetech.com:7075/xdi/graph");
		//if (DIDs.DID_BAUMINUS.equals(did))  return URI.create("http://bauminus.SovereignID.danubetech.com:7076/xdi/graph");

		return null;
	}

	public static URI userXdiEndpoint(String agency, String userDid) {

		if (agency == null) throw new NullPointerException();

		System.out.println("userXdiEndpoint(" + agency + "," + userDid + ")");

		if (DIDs.DID_NTB.equals(agency))  return URI.create("http://ntb.SovereignID.danubetech.com:7071/xdi/cl/" + userDid);
		if (DIDs.DID_RAIFFEISEN.equals(agency))  return URI.create("http://raiffeisen.SovereignID.danubetech.com:7072/xdi/cl/" + userDid);
		if (DIDs.DID_UNIQA.equals(agency))  return URI.create("http://uniqa.SovereignID.danubetech.com:7073/xdi/cl/" + userDid);
		if (DIDs.DID_POST.equals(agency))  return URI.create("http://post.SovereignID.danubetech.com:7074/xdi/cl/" + userDid);
		if (DIDs.DID_SOLLKAUFEN.equals(agency))  return URI.create("http://sollkaufen.SovereignID.danubetech.com:7075/xdi/cl/" + userDid);
		//if (DIDs.DID_BAUMINUS.equals(did))  return URI.create("http://bauminus.SovereignID.danubetech.com:7076/xdi/cl/" + userDid);

		return null;
	}

	public static URI trustanchorEndpoint(String trustanchor) {

		if (trustanchor == null) throw new NullPointerException();

		System.out.println("trustanchorEndpoint(" + trustanchor + ")");

		if (DIDs.DID_NTB.equals(trustanchor))  return URI.create("http://ntb.SovereignID.danubetech.com:7071/trustanchor");
		if (DIDs.DID_RAIFFEISEN.equals(trustanchor))  return URI.create("http://raiffeisen.SovereignID.danubetech.com:7072/trustanchor");
		if (DIDs.DID_UNIQA.equals(trustanchor))  return URI.create("http://uniqa.SovereignID.danubetech.com:7073/trustanchor");
		if (DIDs.DID_POST.equals(trustanchor))  return URI.create("http://post.SovereignID.danubetech.com:7074/trustanchor");
		if (DIDs.DID_SOLLKAUFEN.equals(trustanchor))  return URI.create("http://sollkaufen.SovereignID.danubetech.com:7075/trustanchor");
		//if (DIDs.DID_BAUMINUS.equals(trustanchor))  return URI.create("http://bauminus.SovereignID.danubetech.com:7076/trustanchor");

		return null;
	}
}
