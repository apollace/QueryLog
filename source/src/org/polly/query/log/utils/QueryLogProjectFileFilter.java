package org.polly.query.log.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class QueryLogProjectFileFilter extends FileFilter {

	@Override
	public String getDescription() {
		return "QueryLog project file";
	}

	@Override
	public boolean accept(File f) {
		return f.getName().endsWith(".qlp");
	}

}
