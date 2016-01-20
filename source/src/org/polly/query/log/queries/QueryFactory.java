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

/**
 * This Factory class is used to hide the real implementation used of
 * {@link IQuery}. This class is a singleton.
 * 
 * @author Alessandro Pollace
 */
public class QueryFactory {
	// The unique instance of QueryFactory
	private static QueryFactory instance = new QueryFactory();

	// Currently only the base query exists, for this reason
	private IQuery query = new BaseQuery();

	/**
	 * Hide constructor to prevent the instance creation of this class
	 */
	private QueryFactory() {

	}

	/**
	 * This method is used to return the right query instance
	 * 
	 * @return the query instance
	 */
	public IQuery getQuery() {
		return query;
	}

	/**
	 * Return the unique instance of {@link QueryFactory} class.
	 * 
	 * @return the unique instance of {@link QueryFactory}
	 */
	public static QueryFactory getInstance() {
		return instance;
	}

}
