/*******************************************************************************
 * Copyright (c) 2014 Remain BV. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

/**
 * This class is meant to be running as remote OSGi service. Clients can get
 * hold of this service and ask to start a service for a specific GPIO pin.
 * 
 * @author Wim Jongman
 *
 */
public interface IGPIOPinManager {

	public static int STATUS_OK = 0;
	public static int STATUS_ALREADY_ACTIVE = 1;
	public static int STATUS_NOT_ACTIVE = 2;
	public static int STATUS_ERROR = 3;

	/**
	 * Starts a pin service for the specified pin.
	 * 
	 * @param pPin
	 * @return any of the STATUS* fields defined in this class
	 */
	public int startPinService(int pPin);

	/**
	 * Brings down the service for the specified pin.
	 * 
	 * @param pPin
	 * @return any of the STATUS* fields defined in this class
	 */
	public int endPinService(int pPin);
}
