/*******************************************************************************
 * Copyright (c) 2014 Remain BV and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.raspberrypi.gpio.pinmanager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinManager;
import org.eclipse.ecf.raspberrypi.gpio.IGPIOPinOutput;
import org.eclipse.ecf.raspberrypi.gpio.pi4j.AbstractPinController;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class GPIOPinManager implements IGPIOPinManager {

	private BundleContext fContext;
	private HashMap<Integer, AbstractPinController> fOutputPins = new HashMap<>();
	private ServiceTracker<IGPIOPinOutput, IGPIOPinOutput> fTracker;

	public GPIOPinManager(BundleContext pContext) {
		this.fContext = pContext;
		register();
		registerTracker();
	}

	@Override
	public int startPinService(int pPin) {
		if (fOutputPins.containsKey(pPin)) {
			return STATUS_ALREADY_ACTIVE;
		}
		return createandRunService(pPin);
	}

	private int createandRunService(int pPin) {
		try {
			AbstractPinController pinController = getPinController(pPin);
			fOutputPins.put(pPin, pinController);
			pinController.setup(fContext);
		} catch (Exception e) {
			return STATUS_ERROR;
		}
		return STATUS_OK;
	}

	private int stopService(int pPin) {
		try {
			AbstractPinController pinController = fOutputPins.get(pPin);
			pinController.dispose();
		} catch (Exception e) {
			return STATUS_ERROR;
		}
		return STATUS_OK;
	}

	private AbstractPinController getPinController(final int pPin) {
		return new AbstractPinController() {
			@Override
			public int getPinNumber() {
				return pPin;
			}
		};
	}

	@Override
	public int endPinService(int pPin) {
		if (!fOutputPins.containsKey(pPin)) {
			return STATUS_NOT_ACTIVE;
		}
		return stopService(pPin);
	}

	/**
	 * Registers this manager service defined.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ServiceRegistration<IGPIOPinManager> register() {
		ServiceFactory<IGPIOPinManager> serviceFactory = new ServiceFactory<IGPIOPinManager>() {
			@Override
			public IGPIOPinManager getService(Bundle bundle,
					ServiceRegistration<IGPIOPinManager> registration) {
				return GPIOPinManager.this;
			}

			@Override
			public void ungetService(Bundle bundle,
					ServiceRegistration<IGPIOPinManager> registration,
					IGPIOPinManager service) {
				GPIOPinManager.this.dispose();
			}
		};

		return fContext.registerService(IGPIOPinManager.class, serviceFactory,
				(Dictionary<String, Object>) getProperties());
	}

	public void dispose() {
		for (AbstractPinController controller : fOutputPins.values()) {
			try {
				controller.dispose();
			} catch (Exception e) {
			}
			fOutputPins.remove(controller.getPinNumber());
		}
	}

	private void registerTracker() {
		// Create tracker to print out information from registrations
		fTracker = new ServiceTracker<IGPIOPinOutput, IGPIOPinOutput>(fContext,
				IGPIOPinOutput.class, new TrackerCustomizer());
		fTracker.open();
	}

	/**
	 * Sets the default remote service properties. Override this method if you
	 * want to add additional properties like so:
	 * 
	 * <pre>
	 * protected Map&lt;String, Object&gt; getDefaultPinProps() {
	 * 	Map&lt;String, Object&gt; pinProps = super.getDefaultPinProps();
	 * 	pinProps.put(&quot;my.property&quot;, &quot;my.value&quot;);
	 * }
	 * </pre>
	 * 
	 * @return the properties.
	 */
	public Map<String, Object> getProperties() {
		// Setup properties for export using the ecf generic server
		Map<String, Object> pinProps = new HashMap<String, Object>();
		pinProps.put("service.exported.interfaces", "*");
		pinProps.put("service.exported.configs", "ecf.generic.server");
		pinProps.put("ecf.generic.server.port", "3288");
		try {
			pinProps.put("ecf.generic.server.hostname", InetAddress
					.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		pinProps.put("ecf.exported.async.interfaces", "*");
		Properties systemProps = System.getProperties();
		for (Object pn : systemProps.keySet()) {
			String propName = (String) pn;
			if (propName.startsWith("service.") || propName.startsWith("ecf."))
				pinProps.put(propName, systemProps.get(propName));
		}
		return pinProps;
	}

	private final class TrackerCustomizer implements
			ServiceTrackerCustomizer<IGPIOPinOutput, IGPIOPinOutput> {
		@Override
		public IGPIOPinOutput addingService(
				ServiceReference<IGPIOPinOutput> reference) {
			IGPIOPinOutput pin = fContext.getService(reference);
			AbstractPinController controller = fOutputPins.get(pin.getPinId());
			if (controller != null) {
				System.out.println("PinManager has registered pin "
						+ pin.getPinId() + ".");
				System.out.println("PinManager is now managing "
						+ fOutputPins.size() + " pins.");
			}
			return pin;
		}

		@Override
		public void modifiedService(ServiceReference<IGPIOPinOutput> reference,
				IGPIOPinOutput service) {
		}

		@Override
		public void removedService(ServiceReference<IGPIOPinOutput> reference,
				IGPIOPinOutput pin) {
			System.out.println("PinManager is now managing "
					+ fOutputPins.size() + " pins.");
		}
	}

}
