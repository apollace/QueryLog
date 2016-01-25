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

import org.polly.query.log.queries.IQuery;
import org.polly.query.log.queries.QueryFactory;
import org.polly.query.log.reader.FileReader;
import org.polly.query.log.reader.IReader;
import org.polly.query.log.reader.IReader.ICallback;

import sun.awt.Mutex;

/**
 * 
 * @author Alessandro Pollace
 */
public class QueryController {
	private IQuery query = QueryFactory.getInstance().getQuery();
	private IReader reader = new FileReader();

	private final Mutex mutex = new Mutex();
	private volatile int advancementPecentage = 0;
	private StringBuilder matchedLinesFromLastTime = new StringBuilder();

	/**
	 * This is the implementation of {@link ICallback} used to manage the data
	 * matched
	 * 
	 * @author Alessandro Pollace
	 */
	private class Callback implements ICallback {

		@Override
		public void onData(byte[] bytes, boolean isLast) {
			if (!query.match(bytes))
				return;
			
			mutex.lock();
			matchedLinesFromLastTime.append(new String(bytes)).append("\n");
			mutex.unlock();
		}

		@Override
		public void onProgressChange(int advancementPercentage) {
			QueryController.this.advancementPecentage = advancementPecentage;
		}
	}

	private Callback callback = new Callback();

	public boolean search(String query, String url) {
		advancementPecentage = 0;
		matchedLinesFromLastTime = new StringBuilder();

		// Try to set query
		if (!this.query.setQuery(query)) {
			return false;
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

	public String getNewMatches() {
		String newData;
		mutex.lock();
		newData = matchedLinesFromLastTime.toString();
		matchedLinesFromLastTime = new StringBuilder();
		mutex.unlock();

		return newData;
	}

	public int getAdvancement() {
		return advancementPecentage;
	}

}
