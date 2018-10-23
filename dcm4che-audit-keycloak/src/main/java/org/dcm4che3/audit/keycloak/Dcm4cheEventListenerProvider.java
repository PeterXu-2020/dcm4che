/*
 * *** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * J4Care.
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * *** END LICENSE BLOCK *****
 */
package org.dcm4che3.audit.keycloak;

import org.dcm4che3.net.audit.AuditLogger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Mar 2016
 */
public class Dcm4cheEventListenerProvider implements EventListenerProvider {
    private static final Logger LOG = LoggerFactory.getLogger(Dcm4cheEventListenerProvider.class);
    private final Set<EventType> includedEvents;
    private final KeycloakSession keycloakSession;

    public Dcm4cheEventListenerProvider(Set<EventType> includedEvents, KeycloakSession keycloakSession) {
        this.includedEvents = includedEvents;
        this.keycloakSession = keycloakSession;
    }


    @Override
    public void onEvent(Event event) {
        if (includedEvents != null && includedEvents.contains(event.getType())) {
            try {
                Collection<AuditLogger> loggers = new AuditLoggerFactory().getAuditLoggers();
                if (loggers != null)
                    for (AuditLogger logger : loggers)
                        if (logger.isInstalled())
                            AuditAuth.spoolAuditMsg(event, logger, keycloakSession);
            } catch (Exception e) {
                LOG.warn("Failed to get audit logger", e);
            }
        }
    }

    public void onEvent(AdminEvent adminEvent, boolean b) {
        LOG.warn(".....admin event type is..." + adminEvent);
        LOG.warn(".....admin event operation type is..." + adminEvent.getOperationType());
    }

    public void close() {

    }
}
