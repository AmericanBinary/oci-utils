package oci_utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.*;
import lombok.experimental.Accessors;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

@Data
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LogbackVerbosityMixin {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Logger rootLogger;
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    @Setter(AccessLevel.NONE)
    private int verbosity;

    public LogbackVerbosityMixin() {
        this(Level.WARN);
    }

    public LogbackVerbosityMixin(Level warn) {
        rootLogger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(warn);
    }

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            description = "Increase verbosity. Specify multiple times to increase (-vvv)."
    )
    public void verbosity(boolean[] verbosity) {
        // default is warn, for every other one, set it
        int delta = 0;
        for (boolean b : verbosity) {
            delta += b ? 1 : -1;
        }

        setVerbosity(delta);
    }

    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        List<Level> levels = List.of(Level.OFF, Level.ERROR, Level.INFO, Level.DEBUG, Level.TRACE);
        int defaultLevel = 1;
        int newLevel = defaultLevel + verbosity;
        int legalNewLevel = newLevel < 0
                ? 0
                : newLevel >= levels.size()
                ? levels.size() - 1
                : newLevel;

        rootLogger.setLevel(levels.get(legalNewLevel));
    }
}
