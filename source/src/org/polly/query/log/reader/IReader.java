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

import java.io.IOException;

/**
 * 
 * @author Alessandro Pollace
 */
public interface IReader {

	/**
	 * This interface is used to manage the callback of reader, to receive data
	 * read by {@link IReader} implementation
	 * 
	 * @author Alessandro Pollace
	 */
	public interface ICallback {
		
		/**
		 * Catch the data read by {@link IReader} implementation. This method
		 * must be thread safe. It may be called from a different thread.
		 * 
		 * @param bytes
		 *            the read data
		 * @param isLast
		 *            <code>true</code> if this buffer is the last read buffer,
		 *            <code>false</code> otherwise.
		 */
		public void onData(Byte[] bytes, boolean isLast);

		/**
		 * This method is used to communicate the advancement percentage, if it
		 * is applicable. This method must be thread safe. It may be called from
		 * a different thread.
		 * 
		 * @param advancementPercentage
		 *            the advancement percentage, it is between 0 and 100
		 */
		public void onProgressChange(int advancementPercentage);
	}

	/**
	 * Initialize the reader. This method has to verify the URL take in input
	 * and verify if it is accessible.
	 * 
	 * @param url
	 *            The URL of log source, it could be a file a network stream or
	 *            something other
	 * @throws IOException
	 *             this exception will be throw if the URL is not usable for
	 *             some reason
	 */
	public void init(String url) throws IOException;

	/**
	 * This method unlock all the resources allocated with init method.
	 */
	public void clear();

	/**
	 * This method start the reading process. It may be start or use a different
	 * thread.
	 * 
	 * @param callback
	 *            The instance of {@link ICallback} used to catch the read data
	 */
	public void start(ICallback callback);

	/**
	 * Interrupt the read process. If the reading process is already terminated
	 * this method do nothing.
	 */
	public void stop();
}
