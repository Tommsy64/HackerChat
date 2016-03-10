package io.github.tommsy64.netchat.arguments;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.Data;

@Parameters(commandDescription = "Hosts a Netchat server")
@Data
public class ServerArguments {
    @Parameter(names = { "-port", "-p" }, description = "Port (0 for random)", validateWith = ValidatePort.class)
    private int port = 8888;

    @Parameter(names = "-password", description = "Netchat server password", password = true)
    private String password;

    @Parameter(names = "--help", description = "Show usage message for this command", help = true)
    private boolean help = false;

    @Parameter(names = "-no-log", description = "Don't log chat to console")
    private boolean noLog = false;
}
