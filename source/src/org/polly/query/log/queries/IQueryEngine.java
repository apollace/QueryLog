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
package org.polly.query.log.queries;

import java.util.List;

/**
 * The {@link IQueryEngine} is the interface used to build a new query model. In
 * this system the query contains the matching algorithm.
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
public interface IQueryEngine {
	public enum StatementType {
		PLAIN, START_END_CONTINUE
	};

	/**
	 * Store the user query.
	 * 
	 * @param query
	 *            the query write by user
	 * 
	 * @return <code>true</code> if the query is a valid query,
	 *         <code>false</code> otherwise.
	 */
	@Deprecated
	public boolean setQuery(String query);

	public boolean setQueryParameters(List<String> parameters);
	
	/**
	 * This method verify if the current line match with the user query and
	 * previous log records.
	 * 
	 * @param line
	 *            the current log record
	 * 
	 * @return <code>true</code> if the current line matches, <code>false</code>
	 *         otherwise.
	 */
	public boolean match(String line);

	/**
	 * This method return the type of statement set as string query.
	 * 
	 * @return an enumerative that represents the statement type.
	 */
	public StatementType getStatementType();

	/**
	 * This method returns the name of the query function implemented by the
	 * class.
	 * 
	 * @return a string that represents the name of query function
	 */
	public String getQueryFunctionName();
}
