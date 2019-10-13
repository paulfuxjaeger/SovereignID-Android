package com.danubetech.navigator.util;

import com.danubetech.navigator.R;

import org.SovereignID.common.DIDs;

/**
 * Created by markus on 3/20/18.
 */

public class DIDImages {

    public static int imageResource(String did) {

        if (did == null) throw new NullPointerException();

        if (DIDs.DID_NTB.equals(did)) return R.drawable.logo_ntb;
        if (DIDs.DID_RAIFFEISEN.equals(did)) return R.drawable.logo_raiffeisen;
        if (DIDs.DID_UNIQA.equals(did)) return R.drawable.logo_uniqa;
        if (DIDs.DID_POST.equals(did)) return R.drawable.logo_post;
        if (DIDs.DID_SOLLKAUFEN.equals(did)) return R.drawable.logo_sollkaufen;
        if (DIDs.DID_BAUMINUS.equals(did)) return R.drawable.logo_bauminus;

        return R.drawable.logo_unknown;
    }
}
