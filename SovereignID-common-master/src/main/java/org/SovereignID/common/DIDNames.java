package org.SovereignID.common;

import org.SovereignID.common.DIDs;

/**
 * Created by markus on 3/20/18.
 */

public class DIDNames {

    public static String name(String did) {

        if (did == null) throw new NullPointerException();

		System.out.println("name(" + did + ")");

        if (DIDs.DID_NTB.equals(did)) return "Notartreuhandbank";
        if (DIDs.DID_RAIFFEISEN.equals(did)) return "Raiffeisen";
        if (DIDs.DID_UNIQA.equals(did))  return "Uniqa";
        if (DIDs.DID_POST.equals(did))  return "Post";
        if (DIDs.DID_SOLLKAUFEN.equals(did))  return "SollKaufen";
        if (DIDs.DID_BAUMINUS.equals(did))  return "BauMinus";

        throw new IllegalArgumentException();
    }
}
