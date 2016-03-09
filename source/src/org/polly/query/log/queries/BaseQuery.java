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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseQuery implements IQuery {
	private StatementType ststementType = IQuery.StatementType.PLAIN;

	// This regular expression is required to identify and extract the
	// Start/Continue/End statement
	// Tested with:
	// ${Start;Continue;End}
	// ${S\;tart;Continue;End}
	// ${S\;tart;\$\{Start\;Continue\;End\};End}
	// ${S\;tart;\$\{Start\;Continue\;End\};E\nd}
	//
	private String matchAndExtractStartContinueEndStatement = "(?<!\\\\)\\$(?<!\\\\)\\{(.+?)(?<!\\\\);(.+?)(?<!\\\\);(.+?)(?<!\\\\)\\}";

	private enum Sate {
		START, CONTINUE, END
	};

	private class InternalQuery {
		public Sate currentState = Sate.START;

		public String startWith = null;
		public Pattern startWithCompiled = null;
		public String startWithCompiledStr = null;

		public String continueWith = null;
		public Pattern continueWithCompiled = null;
		public String continueWithCompiledStr = null;

		public String endWith = null;
		public Pattern endWithCompiled = null;
		public String endWithCompiledStr = null;

		public String queryStr = null;
		public Pattern queryRegEx = null;

		public InternalQuery next = null;
		public InternalQuery prev = null;
	}

	private Map<String, String> dynamicValuesReturned = new HashMap<String, String>();

	private static final String REGEX_TOKEN = "regex:";
	private InternalQuery query = new InternalQuery();

	/**
	 * Hide the constructor to prevent the instantiation of this class outside
	 * of this package
	 */
	protected BaseQuery() {

	}

	@Override
	public boolean setQuery(String query) {
		// Restore a clean state
		dynamicValuesReturned.clear();

		// Verify if the current query is a Start/Continue/End statement
		String regexKey = "\\(\\?\\<([0-9a-zA-Z]+)\\>.+?\\)";
		Pattern rexexPatternKey = Pattern.compile(regexKey);
		Matcher mKey = rexexPatternKey.matcher(query);
		while (mKey.find()) {
			for (int i = 0; i < mKey.groupCount(); i++) {
				String extractedValue = mKey.group(i + 1);
				dynamicValuesReturned.put(extractedValue, "FAKE_VAL");
			}
		}

		extractInternalQuery(this.query, query);
		return true;
	}

	private boolean extractInternalQuery(InternalQuery query, String queryStr) {
		// The query has this format: @{Start;continue;end}
		queryStr = queryStr.trim();
		if (!queryStr.startsWith("@{")) {
			// Query is a normal query
			ststementType = StatementType.PLAIN;
			if (isRegex(queryStr)) {
				query.queryRegEx = compileRegEx(queryStr);
			}
			query.queryStr = queryStr;
			return true;
		} else {
			// Query is an advanced query
			ststementType = StatementType.START_END_CONTINUE;
			query.queryStr = null;
		}

		int firstSeparator = queryStr.indexOf(';');
		int lastSeparator = queryStr.lastIndexOf(';');

		query.startWith = queryStr.substring(0, firstSeparator).replace("@{", "");
		query.startWithCompiledStr = compile(query.startWith);
		if (isRegex(query.startWithCompiledStr)) {
			query.startWithCompiled = compileRegEx(query.startWithCompiledStr);
		}

		query.continueWith = queryStr.substring(firstSeparator + 1, lastSeparator);

		query.endWith = queryStr.substring(lastSeparator + 1).replace("}", "");

		if (query.continueWith.startsWith("@{")) {
			query.next = new InternalQuery();
			query.next.prev = query;
			extractInternalQuery(query.next, query.continueWith);
			query.continueWith = null;
		}

		return true;
	}

	@Override
	public boolean match(String line) {
		if (query.queryStr != null) {
			if (query.queryRegEx == null) {
				if (lineMatch(query.queryStr, line)) {
					return true;
				}
			} else {
				if (lineMatchCompiled(query.queryRegEx, line)) {
					return true;
				}
			}

			return false;
		}

		if (query.currentState == Sate.START && manageStart(line)) {
			return true;
		} else if (query.currentState == Sate.CONTINUE && manageEnd(line)) {
			return true;
		} else if (query.currentState == Sate.CONTINUE && manageContinue(line)) {
			return true;
		}

		return false;
	}

	@Override
	public StatementType getStatementType() {
		return ststementType;
	}

	private boolean moveUpContext() {
		if (query.next == null) {
			return false;
		}
		query = query.next;
		return true;
	}

	private boolean moveDownContext() {
		if (query.prev == null) {
			return false;
		}
		query = query.prev;
		return true;
	}

	private boolean manageStart(String line) {
		boolean isMached = false;
		if (query.startWithCompiled == null) {
			isMached = line.contains(query.startWithCompiledStr);
		} else {
			isMached = lineMatchCompiled(query.startWithCompiled, line);
		}

		if (isMached) {
			query.currentState = Sate.CONTINUE;
			if (query.continueWith == null) {
				moveUpContext();
				query.startWithCompiledStr = compile(query.startWith);
				if (isRegex(query.startWithCompiledStr)) {
					query.startWithCompiled = compileRegEx(query.startWithCompiledStr);
				}
			} else {
				query.continueWithCompiledStr = compile(query.continueWith);
				if (isRegex(query.continueWithCompiledStr)) {
					query.continueWithCompiled = compileRegEx(query.continueWithCompiledStr);
				}

				query.endWithCompiledStr = compile(query.endWith);
				if (isRegex(query.endWithCompiledStr)) {
					query.endWithCompiled = compileRegEx(query.endWithCompiledStr);
				}
			}
		}

		return isMached;
	}

	private boolean manageContinue(String line) {
		boolean isMached = false;
		if (query.continueWithCompiled == null) {
			isMached = line.contains(query.continueWithCompiledStr);
		} else {
			isMached = lineMatchCompiled(query.continueWithCompiled, line);
		}
		return isMached;
	}

	private boolean manageEnd(String line) {
		boolean isMached = false;
		if (query.endWithCompiled == null) {
			isMached = line.contains(query.endWithCompiledStr);
		} else {
			isMached = lineMatchCompiled(query.endWithCompiled, line);
		}

		if (isMached) {
			query.currentState = Sate.START;

			if (moveDownContext()) {
				query.endWithCompiledStr = compile(query.endWith);
				if (isRegex(query.endWithCompiledStr)) {
					query.endWithCompiled = compileRegEx(query.endWithCompiledStr);
				}
				manageEnd(line);
			}
		}

		return isMached;
	}

	private boolean isRegex(String query) {
		return query.startsWith(REGEX_TOKEN);
	}

	private Pattern compileRegEx(String query) {
		query = query.replace(REGEX_TOKEN, "");
		return Pattern.compile(query);
	}

	private String compile(String query) {
		String compiled = query;
		String regex = "\\<\\<([0-9a-zA-Z]+)\\>\\>";
		Pattern rexexPattern = Pattern.compile(regex);
		Matcher mContinue = rexexPattern.matcher(compiled);
		while (mContinue.find()) {
			for (int i = 0; i < mContinue.groupCount(); i++) {
				String extractedValue = mContinue.group(i + 1);
				compiled = compiled.replace("<<" + extractedValue + ">>", dynamicValuesReturned.get(extractedValue));
			}
		}

		return compiled;
	}

	private boolean lineMatch(String query, String line) {
		return line.contains(query);
	}

	private boolean lineMatchCompiled(Pattern rexexPattern, String line) {
		boolean isMached = false;
		Matcher m = rexexPattern.matcher(line);
		while (m.find()) {
			for (String key : dynamicValuesReturned.keySet()) {
				try {
					String extractedValue = m.group(key);
					if (extractedValue != null) {
						dynamicValuesReturned.put(key, extractedValue);
					}
				} catch (java.lang.IllegalArgumentException e) {
					// do nothing key not found
				}
			}

			isMached = true;
		}
		return isMached;
	}
}
