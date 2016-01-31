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
package org.polly.query.log.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * The Project controller allow to manage all project settings. A project is a
 * set of properties.
 * 
 * Thanks to this controller you can set a property value or you can receive a
 * notification when a project properties has been changed.
 * 
 * @author Alessandro Pollace
 */
public class ProjectController {
	private Properties projectProperties = new Properties();

	private static final String PROJECT_FORMAT_VERSION_KEY = "ProjectFormatVersion";
	public static final int PROJECT_FORMAT_VERSION = 1;

	public static final String QUERY = "Query";
	public static final String LOGS_FOLDER = "LogFolder";

	private static final Set<String> ADMISSIBLE_KEYS = new HashSet<String>(Arrays.asList(QUERY, LOGS_FOLDER));

	private Map<String, List<PropertyListener>> listeners = new HashMap<String, List<PropertyListener>>();

	public class ProjectException extends Exception {
		private static final long serialVersionUID = 4260760043141736124L;

		public ProjectException(String message) {
			super(message);
		}

		public ProjectException(Throwable cause) {
			super(cause);
		}

		public ProjectException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * This interface must be implemented to receive e notification about
	 * project properties.
	 * 
	 * @author Alessandro Pollace
	 */
	public interface PropertyListener {

		/**
		 * This method notify the value of subscribed properties.
		 * 
		 * @param propertyValue
		 *            This is the notified value
		 */
		public void onChange(String propertyValue);
	}

	/**
	 * Save the project settings at specified path.
	 * 
	 * @param absoluteProjectFilePath
	 *            The absolute file path used to save the project file. This
	 *            path must contains the project filename.
	 * @throws ProjectException
	 */
	public void saveProject(String absoluteProjectFilePath) throws ProjectException {
		projectProperties.setProperty(PROJECT_FORMAT_VERSION_KEY, String.valueOf(PROJECT_FORMAT_VERSION));

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(absoluteProjectFilePath);
			projectProperties.store(fos, "QueryLog project");
		} catch (Exception e) {
			throw new ProjectException(e.getMessage(), e.getCause());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new ProjectException(e.getMessage(), e.getCause());
				}
			}
		}
	}

	/**
	 * Open a project saved previously. When a project is loaded successfully
	 * all listeners receive a notification to update the values.
	 * 
	 * @param absoluteProjectFilePath
	 *            The absolute file path used to open the project file. This
	 *            path must contains the project filename.
	 * @throws ProjectException
	 */
	public void openProject(String absoluteProjectFilePath) throws ProjectException {
		FileInputStream fim = null;
		try {
			fim = new FileInputStream(absoluteProjectFilePath);
			projectProperties.load(fim);
		} catch (Exception e) {
			throw new ProjectException(e.getMessage(), e.getCause());
		} finally {
			if (fim != null) {
				try {
					fim.close();
				} catch (IOException e) {
					throw new ProjectException(e.getMessage(), e.getCause());
				}
			}
		}

		String ProjectVersion = projectProperties.getProperty(PROJECT_FORMAT_VERSION_KEY);
		if (Integer.valueOf(ProjectVersion) != PROJECT_FORMAT_VERSION)
			throw new ProjectException("Incompatible project version, please update QueryLog");

		for (Entry<Object, Object> key : projectProperties.entrySet()) {
			onChange(key.getKey().toString(), key.getValue().toString());
		}
	}

	/**
	 * Set a property into project. After the property set the onChange event
	 * will be raised to notify to all subscribed components the change of
	 * property.
	 * 
	 * @param propertyKey
	 *            the key of property
	 * @param propertyValue
	 *            the value of property
	 */
	public void setProperty(String propertyKey, String propertyValue) {
		if (!ADMISSIBLE_KEYS.contains(propertyKey)) {
			// TODO error
			return;
		}

		projectProperties.setProperty(propertyKey.toString(), propertyValue);
		onChange(propertyKey, propertyValue);
	}

	/**
	 * Get the value of given property.
	 * 
	 * @param propertyKey
	 *            The key of property
	 * @return the value of property
	 */
	public String getProperty(String propertyKey) {
		if (!ADMISSIBLE_KEYS.contains(propertyKey)) {
			// TODO error
			return "";
		}

		return projectProperties.getProperty(propertyKey.toString());
	}

	/**
	 * This method add a listener to a property.
	 * 
	 * @param propertyKey
	 *            The key of property to listen
	 * @param listener
	 *            The listener instance used to notify a data change
	 */
	public void addListener(String propertyKey, PropertyListener listener) {
		if (!ADMISSIBLE_KEYS.contains(propertyKey)) {
			// TODO error
			return;
		}

		List<PropertyListener> list = listeners.get(propertyKey);
		if (list == null) {
			list = new LinkedList<>();
			listeners.put(propertyKey, list);
		}
		list.add(listener);
	}

	private void onChange(String propertyKey, String propertyValue) {
		if (!ADMISSIBLE_KEYS.contains(propertyKey)) {
			// TODO error
			return;
		}

		List<PropertyListener> list = listeners.get(propertyKey);
		if (list == null) {
			// There are no listeners for this property
			return;
		}

		// Notify data to all listeners
		for (PropertyListener listener : list) {
			listener.onChange(propertyValue);
		}
	}

}
