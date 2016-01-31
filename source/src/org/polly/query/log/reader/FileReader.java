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
package org.polly.query.log.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.polly.query.log.utils.ErrorManagedThread;

import sun.awt.Mutex;

/**
 * This class implements the {@link IReader} interface to read the log files
 * contained in a single folder. The files will be reader in alphabetical order
 * one by one.
 * 
 * @author Alessandro Pollace
 */
public class FileReader implements IReader {
	private volatile boolean isRun = false;

	final Mutex mutex = new Mutex();
	private File rootDirectory = null;

	/**
	 * This method is the main function of reader thread.
	 * 
	 * @param callbak
	 *            instance of {@link ICallback} used to send back the results
	 * @throws FileNotFoundException
	 */
	private void threadMain(ICallback callbak) throws IOException {
		File[] logFiles = rootDirectory.listFiles();
		int filesNumber = logFiles.length;
		int currentFileIndex = 0;
		while (isRun || currentFileIndex < filesNumber) {
			BufferedReader br = new BufferedReader(new java.io.FileReader(logFiles[currentFileIndex]));

			String line = null;
			while ((line = br.readLine()) != null) {
				callbak.onData(line, false);
			}

			br.close();
			currentFileIndex++;

			double percentage = (double) currentFileIndex / (double) filesNumber * 100;
			callbak.onProgressChange((int) percentage);
		}
		isRun = false;
	}

	@Override
	public void init(String url) throws IOException {
		mutex.lock();
		rootDirectory = new File(url);
		if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
			rootDirectory = null;
			mutex.unlock();
			throw new IOException("URL does not represent a valid folder path");
		}
		mutex.unlock();
	}

	@Override
	public void clear() {
		mutex.lock();
		rootDirectory = null;
		mutex.unlock();
	}

	@Override
	public void start(final ICallback callback) {
		mutex.lock();
		if (rootDirectory == null) {
			mutex.unlock();
			return;
		}
		mutex.unlock();

		// Start the reading in a new thread
		new ErrorManagedThread() {
			@Override
			public void runErrorManaged() throws Exception {
				mutex.lock();
				try {
					threadMain(callback);
				} catch (Exception e) {
					FileReader.this.stop();
					throw e;
				}
				mutex.unlock();
			};
		}.start();

	}

	@Override
	public void stop() {
		isRun = false;
	}

}
