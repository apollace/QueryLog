/**
 * This file belonging to QueryLog an open source tool to search and trace
 * information contained in your logs.  
 * Copyright (C) 2016  Alessandro Pollace
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.polly.query.log.utils;

import org.polly.query.log.ErrorManager;

/**
 * This is an extension of thread that allow to catch the error in the QueryLog
 * standard way
 * 
 * @author Alessandro Pollace
 */
public abstract class ErrorManagedThread extends Thread {

	@Override
	public void run() {
		try {
			runErrorManaged();
		} catch (Exception e) {
			new ErrorManager(e).setVisible(true);
		}
	}

	/**
	 * This method is the method to implement to execute operations in the new
	 * thread.
	 * 
	 * Using this method all unexpected exception will be managed in the
	 * standard QueryLog way.
	 * 
	 * @throws Exception
	 */
	public abstract void runErrorManaged() throws Exception;

}
