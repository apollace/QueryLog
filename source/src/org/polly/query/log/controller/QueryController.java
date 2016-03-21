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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.polly.query.log.queries.IQueryEngine;
import org.polly.query.log.queries.QueryFactory;
import org.polly.query.log.reader.FileReader;
import org.polly.query.log.reader.IReader;
import org.polly.query.log.reader.IReader.ICallback;

import sun.awt.Mutex;

/**
 * This class controls the query flow management. It take care about query
 * initializations and multiple request management.
 * 
 * @author Alessandro Pollace
 */
public class QueryController {
	private IReader reader = new FileReader();

	private final Mutex mutex = new Mutex();
	private volatile int advancementPercentage = 0;

	private String strQuery = null;
	private IQueryEngine newQuery = null;
	private Map<String, IQueryEngine> queryMapByRequestHeader = new HashMap<String, IQueryEngine>();
	private Map<String, StringBuilder> resultsMapByRequestHeader = new HashMap<String, StringBuilder>();

	/**
	 * This is the implementation of {@link ICallback} used to manage the data
	 * matched
	 * 
	 * @author Alessandro Pollace
	 */
	private class CallbackStartContinueEnd implements ICallback {

		@Override
		public void onData(String data, boolean isLast) {
			StringBuilder currentStringBuilder = null;

			// Check if the current received data match with a new request.
			if (newQuery.match(data)) {
				// Received data is a new request
				mutex.lock();

				String header = new String(data);
				currentStringBuilder = new StringBuilder();
				queryMapByRequestHeader.put(header, newQuery);
				resultsMapByRequestHeader.put(header, currentStringBuilder);
				newQuery = QueryFactory.getInstance().getQuery(strQuery);

				mutex.unlock();
			} else if (true)/*
							 * Add check to verify if current query is a query
							 * START/CONTINUE/END statement
							 */ {
				// Received data is not a new request
				// Verify if the received data matches with one of the older
				// requests
				mutex.lock();
				for (String header : queryMapByRequestHeader.keySet()) {
					if (!queryMapByRequestHeader.get(header).match(data)) {
						continue;
					}

					// Current data match with an older request
					currentStringBuilder = resultsMapByRequestHeader.get(header);
					break;
				}
				mutex.unlock();

			}

			// Verify if there are some data to add
			if (currentStringBuilder == null) {
				// No match found
				return;
			}

			// If so add data
			mutex.lock();
			currentStringBuilder.append(data).append("\n");
			mutex.unlock();
		}

		@Override
		public void onProgressChange(int advancementPercentage) {
			QueryController.this.advancementPercentage = advancementPercentage;
		}
	}

	private class CallbackPlain implements ICallback {
		private static final String DEFAULT_HEADER = "Default";

		@Override
		public void onData(String data, boolean isLast) {
			StringBuilder currentStringBuilder = null;

			// Check if the current received data match with a new request.
			if (newQuery.match(data)) {
				// Received data is a new request
				mutex.lock();

				if ((currentStringBuilder = resultsMapByRequestHeader.get(DEFAULT_HEADER)) == null) {
					currentStringBuilder = new StringBuilder();
					resultsMapByRequestHeader.put(DEFAULT_HEADER, currentStringBuilder);
				}

				mutex.unlock();
			}

			// Verify if there are some data to add
			if (currentStringBuilder == null) {
				// No match found
				return;
			}

			// If so add data
			mutex.lock();
			currentStringBuilder.append(data).append("\n");
			mutex.unlock();

		}

		@Override
		public void onProgressChange(int advancementPercentage) {
			QueryController.this.advancementPercentage = advancementPercentage;
		}
	}

	// This is the instance of callback used to manage the reader callback
	private ICallback callback = null;

	/**
	 * Initialize and start the log search
	 * 
	 * @param query
	 * @param url
	 * @return
	 */
	public boolean search(String query, String url) {
		advancementPercentage = 0;

		queryMapByRequestHeader.clear();
		resultsMapByRequestHeader.clear();

		// Try to set query
		newQuery = QueryFactory.getInstance().getQuery(query);
		// TODO review the statement check flow 
		switch (newQuery.getStatementType()) {
		case PLAIN:
			callback = new CallbackPlain();
			break;
		case START_END_CONTINUE:
			callback = new CallbackStartContinueEnd();
			break;
		}

		// Try to initialize reader
		try {
			this.reader.init(url);
		} catch (IOException e) {
			return false;
		}

		// start reader
		reader.start(callback);
		return true;
	}

	/**
	 * Stop the query search
	 */
	public void stopSearch() {
		reader.stop();
	}

	public String[] getRequestsHeader() {
		Set<String> keys = resultsMapByRequestHeader.keySet();
		String array[] = new String[keys.size()];
		array = keys.toArray(array);
		return array;
	}

	public String getRequestContent(String requestHeader) {
		return resultsMapByRequestHeader.get(requestHeader).toString();
	}

	public int getAdvancement() {
		return advancementPercentage;
	}

}
