package org.SovereignID.common;

import org.SovereignID.common.DIDs;

/**
 * Created by markus on 3/20/18.
 */

public class DIDImages {

    public static String imageResource(String did) {

        if (did == null) throw new NullPointerException();

		System.out.println("imageResource(" + did + ")");

        if (DIDs.DID_NTB.equals(did)) return "/images/logo_ntb.png";
        if (DIDs.DID_RAIFFEISEN.equals(did)) return "/images/logo_raiffeisen.png";
        if (DIDs.DID_UNIQA.equals(did))  return "/images/logo_uniqa.png";
        if (DIDs.DID_POST.equals(did))  return "/images/logo_post.png";;
        if (DIDs.DID_SOLLKAUFEN.equals(did))  return "/images/logo_sollkaufen.png";;
        if (DIDs.DID_BAUMINUS.equals(did))  return "/images/logo_bauminus.png";;

        throw new IllegalArgumentException();
    }
}
