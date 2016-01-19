/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.java.api.demo.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Condition;
import org.eclipse.smarthome.automation.Module;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.automation.handler.BaseModuleHandlerFactory;
import org.eclipse.smarthome.automation.handler.ModuleHandler;
import org.eclipse.smarthome.automation.handler.ModuleHandlerFactory;
import org.eclipse.smarthome.automation.java.api.demo.type.AirConditionerTriggerType;
import org.eclipse.smarthome.automation.java.api.demo.type.StateConditionType;
import org.eclipse.smarthome.automation.java.api.demo.type.LightsTriggerType;
import org.eclipse.smarthome.automation.java.api.demo.type.TemperatureConditionType;
import org.eclipse.smarthome.automation.java.api.demo.type.WelcomeHomeActionType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a simple implementation of the {@link ModuleHandlerFactory}, which is registered as a service.
 *
 * @author Ana Dimova - Initial Contribution
 *
 */
public class WelcomeHomeHandlerFactory extends BaseModuleHandlerFactory {

    public static final String MODULE_HANDLER_FACTORY_NAME = "[JavaAPIDemoHandlerFactory]";
    private static final Collection<String> TYPES;

    static {
        List<String> temp = new ArrayList<String>();
        temp.add(WelcomeHomeActionType.UID);
        temp.add(StateConditionType.UID);
        temp.add(TemperatureConditionType.UID);
        temp.add(AirConditionerTriggerType.UID);
        temp.add(LightsTriggerType.UID);
        TYPES = Collections.unmodifiableCollection(temp);
    }

    @SuppressWarnings("rawtypes")
    private ServiceRegistration factoryRegistration;
    private Map<String, WelcomeHomeTriggerHandler> triggerHandlers;
    private Logger logger = LoggerFactory.getLogger(WelcomeHomeHandlerFactory.class);

    public WelcomeHomeHandlerFactory(BundleContext bc) {
        super(bc);
        triggerHandlers = new HashMap<String, WelcomeHomeTriggerHandler>();
    }

    @Override
    public Collection<String> getTypes() {
        return TYPES;
    }

    public void register(BundleContext bc) {
        factoryRegistration = bc.registerService(ModuleHandlerFactory.class.getName(), this, null);
    }

    public void unregister() {
        factoryRegistration.unregister();
        factoryRegistration = null;
    }

    public WelcomeHomeTriggerHandler getTriggerHandler(String uid) {
        return triggerHandlers.get(uid);
    }

    @Override
    protected ModuleHandler internalCreate(Module module, String ruleUID) {
        ModuleHandler moduleHandler = null;
        if (WelcomeHomeActionType.UID.equals(module.getTypeUID())) {
            moduleHandler = new WelcomeHomeActionHandler((Action) module);
        } else if (StateConditionType.UID.equals(module.getTypeUID())) {
            moduleHandler = new StateConditionHandler((Condition) module);
        } else if (TemperatureConditionType.UID.equals(module.getTypeUID())) {
            moduleHandler = new TemperatureConditionHandler((Condition) module);
        } else if (AirConditionerTriggerType.UID.equals(module.getTypeUID())
                || LightsTriggerType.UID.equals(module.getTypeUID())) {
            moduleHandler = new WelcomeHomeTriggerHandler((Trigger) module);
            triggerHandlers.put(ruleUID, (WelcomeHomeTriggerHandler) moduleHandler);
        } else {
            logger.error(MODULE_HANDLER_FACTORY_NAME + "Not supported moduleHandler: {}", module.getTypeUID());
        }
        return moduleHandler;
    }
}
