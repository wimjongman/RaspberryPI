/*******************************************************************************
 * Copyright (c) 2014 Remain BV and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.raspberrypi.gpio.pinmanager;

import org.eclipse.ecf.raspberrypi.gpio.pi4j.AbstractPinController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractPinController implements BundleActivator {

	@Override
	public void start(BundleContext pBundleContext) throws Exception {
		((AbstractPinController) this).setup(pBundleContext);
	}

	@Override
	public void stop(BundleContext pBundleContext) throws Exception {
		((AbstractPinController) this).dispose();
	}

	@Override
	public int getPinNumber() {
		return 0;
	}
}