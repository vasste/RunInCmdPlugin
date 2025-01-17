package org.turbanov.execution.cmd;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner;
import org.jetbrains.plugins.terminal.TerminalProcessOptions;
import org.jetbrains.plugins.terminal.TerminalView;
import com.intellij.openapi.project.Project;
import com.intellij.terminal.JBTerminalWidget;
import com.pty4j.PtyProcess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrey Turbanov
 */
public class TerminalRunner {
    public static void runInIdeaTerminal(@NotNull Project project, @NotNull String[] command, @NotNull String classPath, @NotNull String workingDirectory) {
        TerminalView terminalView = TerminalView.getInstance(project);
        LocalTerminalDirectRunner runner = new LocalTerminalDirectRunner(project) {
            @Override
            protected PtyProcess createProcess(@Nullable String directory, @Nullable String commandHistoryFilePath) throws ExecutionException {
                return createProcessImpl(classPath, command, workingDirectory);
            }


            @Override
            public @NotNull PtyProcess createProcess(@NotNull TerminalProcessOptions options, @Nullable JBTerminalWidget widget) throws ExecutionException {
                return createProcessImpl(classPath, command, workingDirectory);
            }
        };
        terminalView.createNewSession(runner);
    }

    private static PtyProcess createProcessImpl(@NotNull String classPath, @NotNull String[] command, @NotNull String workingDirectory) throws ExecutionException {
        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.put("CLASSPATH", classPath);
        try {
            return PtyProcess.exec(command, envs, workingDirectory);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }
}
