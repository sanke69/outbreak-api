/**
 * OutBreak API - Covid-19
 * Copyright (C) 2020-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.reporting.sdk.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.reporting.api.Report;

class MotherFuckerException extends RuntimeException {
	private static final long serialVersionUID = -1758249863163171708L;

	MotherFuckerException() {
		super("AAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaah");
	}

}

public abstract class ReportDownloader<T extends Report> {
	private final Path	downloadPath, storagePath;

	protected ReportDownloader(Path _storagePath, Path _downloadPath) {
		super();
		storagePath  = _storagePath;
		downloadPath = _downloadPath;
	}

    public abstract URL                         url();
    public abstract String                      filePrefix();
    public abstract String                      fileExtension();

    public abstract Collection<? extends T>		parse(Path _records);

    public final Collection<? extends T>		getRecords() throws IOException {
    	Path file = null;
    	
    	if((file = download()) == null)
    		file = previousVersion();
    	else
    		persistentStore(file);

    	if(file == null)
    		throw new MotherFuckerException();

    	return parse(file);
    }

    protected final Path 						download() throws IOException {
		LocalDateTime     today          = LocalDateTime.now();
		DateTimeFormatter format         = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss.SSS");

		String            prefix         = filePrefix() + '-' + today.format(format);
		String            ext            = '.' + fileExtension();

		Path              tmpFile        = Files.createTempFile(downloadPath(), prefix, ext);
		tmpFile.toFile().deleteOnExit();

		return wget( url(), tmpFile ) ? tmpFile : null;
    }
	protected final void 						persistentStore(Path _tempFile) {
		LocalDate         today          = LocalDate.now();
		DateTimeFormatter format         = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String            prefix         = filePrefix() + '-' + today.format(format);
		String            ext            = '.' + fileExtension();

		Path              persistentFile = storagePath.resolve(prefix + ext);

		try {
			if(!storagePath.toFile().exists())
				storagePath.toFile().mkdirs();

			if(persistentFile.toFile().exists())
				if( (Files.mismatch(persistentFile, _tempFile) == -1) )
					return ;
				else
					Files.copy(_tempFile, persistentFile, StandardCopyOption.COPY_ATTRIBUTES);
			else
				Files.copy(_tempFile, persistentFile, StandardCopyOption.COPY_ATTRIBUTES);

		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	protected final Path 						previousVersion() {
		DateTimeFormatter    format  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		SortedSet<LocalDate> records = storageRecords();

		return records.isEmpty() ? null : storagePath.resolve(filePrefix() + '-' + records.first().format(format) + '.' + fileExtension());
	}

	protected final Path 						downloadPath() {
		return downloadPath != null ? downloadPath : defaultDownloadPath();
	}
	protected final Path 						downloadPath(String _subFolder) {
		try {
			return Files.createTempDirectory(downloadPath(), _subFolder);
		} catch (IOException e) { throw new MotherFuckerException(); }
	}
	protected final Path 						storagePath() {
		return storagePath != null ? storagePath : defaultStoragePath();
	}
	protected final Path 						storagePath(String _subFolder) {
		return storagePath().resolve(_subFolder);
	}

	protected final SortedSet<LocalDate> 		storageRecords() {
	    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + filePrefix() + "-**.{" + fileExtension() + "}");

		try {
			Files.walk(storagePath)
					.filter(pathMatcher::matches)
					.map(p -> {
						String group_path = "([\\w -/()]*)";
						String group_date = "(?<year>(19|20)[0-9][0-9])([- /.])(?<month>0[1-9]|1[012])\\" + 4 + "(?<day>0[1-9]|[12][0-9]|3[01])";
						String group_file = "("+ (filePrefix() + '-') +")" + group_date;
						String group_ext  = "(.csv)";
							
					    final String  regexp  = group_path + group_file + group_ext;
					    final Pattern pattern = Pattern.compile(regexp);
					    final Matcher matcher = pattern.matcher(p.getFileName().toString());
	
				        if(matcher.find()) {
//			        		int from = matcher.start(), to = matcher.end();

				        	int day   = Integer.valueOf( matcher.group("day") );
				        	int month = Integer.valueOf( matcher.group("month") );
				        	int year  = Integer.valueOf( matcher.group("year") );
				        	
				        	return LocalDate.of(year, month, day);
				        }
	
				        return LocalDate.MAX;
					})
					.filter(ld -> !ld.equals(LocalDate.MAX))
					.collect(Collectors.toCollection(() -> new TreeSet<LocalDate>()));

		} catch (IOException e) { e .printStackTrace(); }

		return Collections.emptySortedSet();
	}

	protected static final Path 				defaultDownloadPath() {
		return Paths.get( System.getProperty("java.io.tmpdir") );
	}
	protected static final Path 				defaultDownloadPath(String _subFolder) {
		try {
			return Files.createTempDirectory(defaultDownloadPath(), _subFolder);
		} catch (IOException e) { throw new MotherFuckerException(); }
	}
	protected static final Path 				defaultStoragePath() {
		return Paths.get("/ssd/share/data");
	}
	protected static final Path 				defaultStoragePath(String _subFolder) {
		return defaultStoragePath().resolve(_subFolder);
	}

	protected static final URL  				urlNoException(String _url) {
		try {
			return new URL(_url);
		} catch (IOException e) {
			throw new MotherFuckerException();
		}
	}

	protected static final boolean 				wget(URL _url, Path _path) {
		InputStream     is  = null;
		OutputStream    os  = null;

		int    nb_read = 0;
		byte[] buffer  = new byte[4096];
		try {
			is = _url.openStream();
			os = new FileOutputStream(_path.toFile(), false);

			while((nb_read = is.read(buffer)) > 0)
				os.write(buffer, 0, nb_read);

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return false;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;

		} finally {

			try { is.close();
			} catch (IOException ioe) { }

			try { os.close();
			} catch (IOException ioe) { return false; }

		}

		return true;
	}

}
