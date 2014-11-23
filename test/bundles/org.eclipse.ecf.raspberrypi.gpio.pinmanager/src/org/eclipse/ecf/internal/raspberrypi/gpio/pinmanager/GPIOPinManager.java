package org.eclipse.ecf.internal.raspberrypi.gpio.pinmanager;

import java.util.HashMap;

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
				// e.printStackTrace();
			}
		};

		return fContext.registerService(IGPIOPinManager.class, serviceFactory,
				null);
	}

	protected void dispose() {
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
