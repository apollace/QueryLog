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
package org.polly.query.log;

/**
 * The {@link IQuery} is the interface used to build a new query model. In this
 * system the query contains the matching algorithm.
 * 
 * In the system must exists a reader, this reader read log files and than ask
 * to query if the current line matches or not with the current user query.
 * 
 * The match maybe context sensitive, for this reason the sequence of log record
 * must be respected.
 * 
 * This class is not thread safe.
 * 
 * @author Alessandro Pollace
 */
public interface IQuery {

	/**
	 * Store the user query.
	 * 
	 * @param query
	 *            the query write by user
	 * 
	 * @return <code>true</code> if the query is a valid query,
	 *         <code>false</code> otherwise.
	 */
	public boolean setQuery(String query);

	/**
	 * This method verify if the current line match with the user query and
	 * previous log records.
	 * 
	 * @param line
	 *            the current line of file;
	 * 
	 * @return <code>true</code> if the current line matches, <code>false</code>
	 *         otherwise.
	 */
	public boolean match(String line);
}
