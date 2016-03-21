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
 * This implementation is used to match a plain string.
 * 
 * @author Alessandro Pollace
 */
public class PlainQueryEngine implements IQueryEngine {
	private String query = "";

	@Override
	public boolean setQuery(String query) {
		this.query = query;
		return false;
	}

	@Override
	public boolean match(String line) {
		return line.contains(query);
	}

	@Override
	public StatementType getStatementType() {
		return StatementType.PLAIN;
	}

	@Override
	public String getQueryFunctionName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setQueryParameters(List<String> parameters) {
		// TODO Auto-generated method stub
		return false;
	}

}
