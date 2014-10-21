/*******************************************************************************
 * Copyright (c) 2014 Remain B.V. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.raspberrypi.gpio;

import java.util.concurrent.CompletableFuture;

/**
 * LM35 is a temperature measurement device.
 * 
 * @author Wim Jongman
 * @see ILM35
 *
 */
public interface ILM35Async {

	/**
	 * Temperature changed on the specified host.
	 * 
	 * @param data
	 */
	public CompletableFuture<Void> setTemperatureAsync(String pHost, double pTemperature);

}