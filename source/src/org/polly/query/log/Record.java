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

import java.io.File;

public class Record {
	File file;
	int lineNumber;
	String line;

	public Record(File file, int lineNumber, String line) {
		this.file = file;
		this.lineNumber = lineNumber;
		this.line = line;
	}

	@Override
	public String toString() {
		return line;
	}

	public File getFile() {
		return file;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
