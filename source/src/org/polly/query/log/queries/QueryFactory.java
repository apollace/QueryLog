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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Factory class is used to hide the real implementation used of
 * {@link IQueryEngine}. This class is a singleton.
 * 
 * @author Alessandro Pollace
 */
public class QueryFactory {
	// The unique instance of QueryFactory
	private static QueryFactory instance = new QueryFactory();

	// This regular expression is used to identify and extract name and
	// parameters from a function query.
	// It extracts two group the first one the name of the function and the
	// second one the list of parameters.
	private static final String FUNCTION_IDENTIFIER_RE = "(?[A-Za-z]+[0-9])(?<!\\)((?.?)(?<!\\))";
	private static final Pattern FUNCTION_IDENTIFIER_PATH = Pattern.compile(FUNCTION_IDENTIFIER_RE);
	private static final int FUNCTION_NAME_INDEX = 0;
	private static final int FUNCTION_PARAMS_INDEX = 1;

	private static final String PARAMETERS_IDENTIFIER_RE = "((?<!\\)\".+?(?<!\\)\")";
	private static final Pattern PARAMETERS_IDENTIFIER_PATH = Pattern.compile(PARAMETERS_IDENTIFIER_RE);

	private static class QueryBuilder<T> {
		private Class<T> c = null;

		public QueryBuilder(Class<T> c) {
			this.c = c;
		}

		public T getQueryInstance() throws InstantiationException, IllegalAccessException {
			return c.newInstance();
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Map<String, QueryBuilder> BUILDERS = new HashMap<String, QueryBuilder>() {
		private static final long serialVersionUID = 2692764292200940047L;
		{
			put("regex", new QueryBuilder<RegexQueryEngine>(RegexQueryEngine.class));
		}
	};

	private List<String> extractFunctionParameters(String parameters) {
		// Extract parameters
		List<String> parametersList = new ArrayList<String>();

		Matcher m = PARAMETERS_IDENTIFIER_PATH.matcher(parameters);
		for (int i = 0; i < m.groupCount(); i++)
			parametersList.add(m.group(i));

		return parametersList;
	}

	private IQueryEngine buildQueryEngineInstance(String functionName) {
		// Build query instance
		@SuppressWarnings("rawtypes")
		QueryBuilder builder = BUILDERS.get(functionName);
		if (builder == null) {
			// TODO manage error
		}

		IQueryEngine engine = null;
		try {
			engine = (IQueryEngine) builder.getQueryInstance();
		} catch (InstantiationException e) {
			// TODO manage error
		} catch (IllegalAccessException e) {
			// TODO manage error
		}
		return engine;
	}

	/**
	 * Hide constructor to prevent the instance creation of this class
	 */
	private QueryFactory() {

	}

	/**
	 * This method is used to return a query instance
	 * 
	 * @return the query instance
	 */
	public IQueryEngine getQuery(String query) {
		// Extract the function name and instantiate the right query type;
		// If no function called in the query use just the PlainQueryEngine
		Matcher m = FUNCTION_IDENTIFIER_PATH.matcher(query);

		if (!m.find()) {
			// The query is a simple query not a function query
			return new PlainQueryEngine();
		}

		// Get the function name
		String functionName = m.group(FUNCTION_NAME_INDEX);

		// get the function parameters
		String parameters = m.group(FUNCTION_PARAMS_INDEX);

		IQueryEngine engine = buildQueryEngineInstance(functionName);
		List<String> parametersList = extractFunctionParameters(parameters);
		engine.setQueryParameters(parametersList);
		return engine;
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
