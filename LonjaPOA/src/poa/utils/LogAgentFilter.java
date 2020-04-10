package poa.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogAgentFilter implements Filter {
	public boolean isLoggable(LogRecord record) {
		return record.getLoggerName().contains("poa");
	}
}