package io.github.tommsy64.netchat.arguments;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.Data;

@Parameters(commandDescription = "Connects to a Netchat server")
@Data
public class ClientArguments {
    @Parameter(names = { "-host", "-h" }, description = "The NetChat host to connect to", required = true)
    private String host;

    @Parameter(names = { "-port", "-p" }, validateWith = ValidatePort.class)
    private int port = 8888;

    @Parameter(names = "-password", description = "Netchat server password", password = true)
    private String password;

    @Parameter(names = "--help", description = "Show usage message for this command", help = true)
    private boolean help;
}
