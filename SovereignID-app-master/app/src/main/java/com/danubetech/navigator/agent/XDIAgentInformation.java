package com.danubetech.navigator.agent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.apache.commons.codec.binary.Hex;
import org.SovereignID.common.DIDXDI;
import org.SovereignID.common.DIDs;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.schema.Schema;

import java.net.URI;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;
import xdi2.core.constants.XDIConstants;
import xdi2.core.security.ec25519.crypto.SHA256Provider;
import xdi2.core.security.ec25519.util.EC25519Base58;
import xdi2.core.security.ec25519.util.EC25519CloudNumberUtil;
import xdi2.core.security.ec25519.util.EC25519KeyPairGenerator;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public class XDIAgentInformation {

    public static final String SHARED_PREFERENCES_NAME = "SovereignID";

    public static final String KEY_SEED = "seed";
    public static final String KEY_DID = "did";
    public static final String KEY_DID_PUBLIC_KEY = "didPublicKey";
    public static final String KEY_DID_PRIVATE_KEY = "didPrivateKey";
    public static final String KEY_APP_SESSION_CID = "appSessionCid";
    public static final String KEY_APP_SESSION_PRIVATE_KEY = "appSessionPrivateKey";
    public static final String KEY_APP_SESSION_LINK_CONTRACT = "appSessionLinkContract";
    public static final String KEY_XDI_ENDPOINT_URI = "xdiEndpointUri";
    public static final String KEY_TRUSTANCHOR_URI = "trustanchorUri";
    public static final String KEY_SCHEMAS = "schemas";
    public static final String KEY_SCHEMAEVENTS = "schemaevents";

    public static String agency;
    public static String trustanchor;

    private String seed;
    private CloudNumber did;
    private byte[] didPublicKey;
    private byte[] didPrivateKey;
    private CloudNumber appSessionCid;
    private byte[] appSessionPrivateKey;
    private XDIAddress appSessionLinkContract;
    private URI xdiEndpointUri;
    private URI trustanchorUri;
    private Map<String, Schema> schemas;
    private List<SchemaEvent> schemaEvents;

    public static XDIAgentInformation create(Context context, String seed) {

        if (seed == null) {

            final StringBuilder sb = new StringBuilder();
            byte[] entropy = new byte[Words.TWELVE.byteLength()];
            new SecureRandom().nextBytes(entropy);
            new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, new MnemonicGenerator.Target() {

                @Override
                public void append(CharSequence string) { sb.append(string); }
            });

            seed = sb.toString();
        }

        byte[] didPublicKey = new byte[32], didPrivateKey = new byte[32];
        CloudNumber did;

        try {

            EC25519KeyPairGenerator.generateEC25519KeyPairFromSeed(didPublicKey, didPrivateKey, seed.substring(0, 32).replace(" ", "X").getBytes("UTF-8"));
            did = CloudNumber.create("=!:did:sov:" + EC25519Base58.encode(Arrays.copyOf(didPublicKey, 16)));
        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage(), ex);
        }

        byte[] appSessionPublicKey = new byte[32], appSessionPrivateKey = new byte[32];
        CloudNumber appSessionCid;

        try {

            EC25519KeyPairGenerator.generateEC25519KeyPair(appSessionPublicKey, appSessionPrivateKey);
            appSessionCid = EC25519CloudNumberUtil.createEC25519CloudNumber(XDIConstants.CS_INSTANCE_UNORDERED, appSessionPublicKey);
        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage(), ex);
        }

        XDIAgentInformation xdiAgentInformation = new XDIAgentInformation();
        xdiAgentInformation.seed = seed;
        xdiAgentInformation.did = did;
        xdiAgentInformation.didPublicKey = didPublicKey;
        xdiAgentInformation.didPrivateKey = didPrivateKey;
        xdiAgentInformation.appSessionCid = appSessionCid;
        xdiAgentInformation.appSessionPrivateKey = appSessionPrivateKey;
        xdiAgentInformation.appSessionLinkContract = null;
        xdiAgentInformation.xdiEndpointUri = DIDXDI.userXdiEndpoint(agency, did.toString());
        xdiAgentInformation.trustanchorUri = DIDXDI.trustanchorEndpoint(trustanchor);
        xdiAgentInformation.schemas = new HashMap<String, Schema>();
        xdiAgentInformation.schemaEvents = new ArrayList<SchemaEvent>();

        return xdiAgentInformation;
    }

    public static XDIAgentInformation load(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

        String seedString = sharedPreferences.getString(KEY_SEED, null);
        String didString = sharedPreferences.getString(KEY_DID, null);
        String didPublicKeyString = sharedPreferences.getString(KEY_DID_PUBLIC_KEY, null);
        String didPrivateKeyString = sharedPreferences.getString(KEY_DID_PRIVATE_KEY, null);
        String appSessionCidString = sharedPreferences.getString(KEY_APP_SESSION_CID, null);
        String appSessionPrivateKeyString = sharedPreferences.getString(KEY_APP_SESSION_PRIVATE_KEY, null);
        String appSessionLinkContractString = sharedPreferences.getString(KEY_APP_SESSION_LINK_CONTRACT, null);
        String xdiEndpointUriString = sharedPreferences.getString(KEY_XDI_ENDPOINT_URI, null);
        String trustanchorUriString = sharedPreferences.getString(KEY_TRUSTANCHOR_URI, null);
        Set<String> schemasString = sharedPreferences.getStringSet(KEY_SCHEMAS, null);
        Set<String> schemaEventsString = sharedPreferences.getStringSet(KEY_SCHEMAEVENTS, null);

        if (seedString == null) return null;
        if (didString == null) return null;
        if (didPublicKeyString == null) return null;
        if (didPrivateKeyString == null) return null;
        if (appSessionCidString == null) return null;
        if (appSessionPrivateKeyString == null) return null;
        if (xdiEndpointUriString == null) return null;

        XDIAgentInformation xdiAgentInformation = new XDIAgentInformation();

        try {

            xdiAgentInformation.seed = seedString;
            xdiAgentInformation.did = CloudNumber.create(didString);
            xdiAgentInformation.didPublicKey = Hex.decodeHex(didPublicKeyString.toCharArray());
            xdiAgentInformation.didPrivateKey = Hex.decodeHex(didPrivateKeyString.toCharArray());
            xdiAgentInformation.appSessionCid = CloudNumber.create(appSessionCidString);
            xdiAgentInformation.appSessionPrivateKey = Hex.decodeHex(appSessionPrivateKeyString.toCharArray());
            if (appSessionLinkContractString != null) xdiAgentInformation.appSessionLinkContract = XDIAddress.create(appSessionLinkContractString);
            xdiAgentInformation.xdiEndpointUri = new URI(xdiEndpointUriString);
            if (trustanchorUriString != null) xdiAgentInformation.trustanchorUri = new URI(trustanchorUriString);
            xdiAgentInformation.schemas = new HashMap<String, Schema> ();
            for (String schemaString : schemasString) { Schema schema = Schema.fromJson(schemaString); xdiAgentInformation.schemas.put(schema.getId(), schema); }
            xdiAgentInformation.schemaEvents = new ArrayList<SchemaEvent> ();
            for (String schemaEventString : schemaEventsString) { SchemaEvent schemaEvent = SchemaEvent.fromJson(schemaEventString); xdiAgentInformation.schemaEvents.add(schemaEvent); }
            Collections.sort(xdiAgentInformation.schemaEvents);
        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage(), ex);
        }

        return xdiAgentInformation;
    }

    public void save(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SEED, this.getSeed().toString());
        editor.putString(KEY_DID, this.getDid().toString());
        editor.putString(KEY_DID_PUBLIC_KEY, String.valueOf(Hex.encodeHex(this.didPublicKey)));
        editor.putString(KEY_DID_PRIVATE_KEY, String.valueOf(Hex.encodeHex(this.didPrivateKey)));
        editor.putString(KEY_APP_SESSION_CID, this.getAppSessionCid().toString());
        editor.putString(KEY_APP_SESSION_PRIVATE_KEY, String.valueOf(Hex.encodeHex(this.appSessionPrivateKey)));
        if (this.getAppSessionLinkContract() != null) editor.putString(KEY_APP_SESSION_LINK_CONTRACT, this.getAppSessionLinkContract().toString());
        editor.putString(KEY_XDI_ENDPOINT_URI, this.getXdiEndpointUri().toString());
        if (this.getTrustanchorUri() != null) editor.putString(KEY_TRUSTANCHOR_URI, this.getTrustanchorUri().toString());
        Set<String> schemasString = new HashSet<String> ();
        for (Schema schema : this.getSchemas().values()) schemasString.add(schema.toJson());
        editor.putStringSet(KEY_SCHEMAS, schemasString);
        Set<String> schemaEventsString = new HashSet<String> ();
        for (SchemaEvent schemaEvent : this.getSchemaEvents()) schemaEventsString.add(schemaEvent.toJson());
        editor.putStringSet(KEY_SCHEMAEVENTS, schemaEventsString);
        editor.apply();
    }

    public static void remove(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SEED);
        editor.remove(KEY_DID);
        editor.remove(KEY_DID_PUBLIC_KEY);
        editor.remove(KEY_DID_PRIVATE_KEY);
        editor.remove(KEY_APP_SESSION_CID);
        editor.remove(KEY_APP_SESSION_PRIVATE_KEY);
        editor.remove(KEY_APP_SESSION_LINK_CONTRACT);
        editor.remove(KEY_XDI_ENDPOINT_URI);
        editor.remove(KEY_TRUSTANCHOR_URI);
        editor.remove(KEY_SCHEMAS);
        editor.remove(KEY_SCHEMAEVENTS);
        editor.apply();
    }

    public boolean isConnected() {

        if (this.getSeed() == null) return false;
        if (this.getDid() == null) return false;
        if (this.getDidPublicKey() == null) return false;
        if (this.getDidPrivateKey() == null) return false;
        if (this.getAppSessionCid() == null) return false;
        if (this.getAppSessionPrivateKey() == null) return false;
        if (this.getXdiEndpointUri() == null) return false;
        if (this.getTrustanchorUri() == null) return false;
        if (this.getAppSessionLinkContract() == null) return false;

        return true;
    }

    public <S extends Schema> S findSchema(Class<S> cl) {

        for (Schema schema : this.getSchemas().values()) {

            if (cl.isAssignableFrom(schema.getClass())) return (S) schema;
        }

        return null;
    }

    public void acceptSchema(Schema newSchema) {

        for (Iterator<Schema> i = this.getSchemas().values().iterator(); i.hasNext(); ) {

            Schema schema = i.next();
            if (newSchema.getClass().equals(schema.getClass()) &&
                    newSchema.getIssuer() != null &&
                    newSchema.getIssuer().equals(schema.getIssuer()))
                i.remove();
        }

        this.getSchemas().put(newSchema.getId(), newSchema);
    }

    public void addSchemaEvent(SchemaEvent schemaEvent) {

        this.getSchemaEvents().add(schemaEvent);
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public CloudNumber getDid() {
        return did;
    }

    public void setDid(CloudNumber did) {
        this.did = did;
    }

    public byte[] getDidPublicKey() {
        return didPublicKey;
    }

    public void setDidPublicKey(byte[] didPublicKey) {
        this.didPublicKey = didPublicKey;
    }

    public byte[] getDidPrivateKey() {
        return didPrivateKey;
    }

    public void setDidPrivateKey(byte[] didPrivateKey) {
        this.didPrivateKey = didPrivateKey;
    }

    public CloudNumber getAppSessionCid() {
        return appSessionCid;
    }

    public void setAppSessionCid(CloudNumber appSessionCid) {
        this.appSessionCid = appSessionCid;
    }

    public byte[] getAppSessionPrivateKey() {
        return appSessionPrivateKey;
    }

    public void setAppSessionPrivateKey(byte[] appSessionPrivateKey) {
        this.appSessionPrivateKey = appSessionPrivateKey;
    }

    public XDIAddress getAppSessionLinkContract() {
        return appSessionLinkContract;
    }

    public void setAppSessionLinkContract(XDIAddress appSessionLinkContract) {
        this.appSessionLinkContract = appSessionLinkContract;
    }

    public URI getXdiEndpointUri() {
        return xdiEndpointUri;
    }

    public void setXdiEndpointUri(URI xdiEndpointUri) {
        this.xdiEndpointUri = xdiEndpointUri;
    }

    public URI getTrustanchorUri() {
        return trustanchorUri;
    }

    public void setTrustanchorUri(URI trustanchorUri) {
        this.trustanchorUri = trustanchorUri;
    }

    public Map<String, Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    public List<SchemaEvent> getSchemaEvents() {
        return schemaEvents;
    }

    public void setSchemaEvents(List<SchemaEvent> schemaEvents) {
        this.schemaEvents = schemaEvents;
    }
}
