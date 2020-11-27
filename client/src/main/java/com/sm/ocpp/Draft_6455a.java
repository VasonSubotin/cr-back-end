package com.sm.ocpp;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.protocols.IProtocol;

import java.util.*;

public class Draft_6455a extends Draft_6455 {

    private Map<String, Object> credentials;

    public Draft_6455a(List<IExtension> inputExtensions, List<IProtocol> inputProtocols) {
        super(inputExtensions, inputProtocols);
    }

    @Override
    public HandshakeState acceptHandshakeAsServer(ClientHandshake handshakedata) throws InvalidHandshakeException {
        if (credentials != null) {
            String basic = handshakedata.getFieldValue("Authorization");
            if (basic == null || basic.isEmpty() || !basic.startsWith("Basic ")) {
                throw new InvalidHandshakeException("Access denied!");
            }
            basic = basic.substring(6);

            if(!credentials.containsKey(basic)){
                throw new InvalidHandshakeException("Invalid user or password!");
            }

            //basic = new String(Base64.getDecoder().decode(basic));

            //Basic YWFhYWFhOm1heDEyMzQ1cXdlcnQ=
        }

        return super.acceptHandshakeAsServer(handshakedata);
    }

    @Override
    public Draft copyInstance() {
        ArrayList<IExtension> newExtensions = new ArrayList();
        Iterator var2 = this.getKnownExtensions().iterator();

        while (var2.hasNext()) {
            IExtension extension = (IExtension) var2.next();
            newExtensions.add(extension.copyInstance());
        }

        ArrayList<IProtocol> newProtocols = new ArrayList();
        Iterator var6 = this.getKnownProtocols().iterator();

        while (var6.hasNext()) {
            IProtocol protocol = (IProtocol) var6.next();
            newProtocols.add(protocol.copyInstance());
        }
        Draft_6455a draft = new Draft_6455a(newExtensions, newProtocols);
        draft.setCredentials(credentials);
        return draft;
    }

    public void setCredentials(Map<String, Object> credentials) {
        this.credentials = credentials;
    }
}
