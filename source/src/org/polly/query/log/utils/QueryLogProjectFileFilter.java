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
package org.polly.query.log.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class QueryLogProjectFileFilter extends FileFilter {

	@Override
	public String getDescription() {
		return "QueryLog project file (qpl)";
	}

	@Override
	public boolean accept(File f) {
		return f.getName().toLowerCase().endsWith(".qlp");
	}

}