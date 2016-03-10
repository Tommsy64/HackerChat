package io.github.tommsy64.hackerchat.arguments;

import com.beust.jcommander.Parameter;

import lombok.Data;

@Data
public class NetchatArguments {
    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = { "--version", "-version", "-v" }, description = "Prints out the version info", help = true)
    private boolean version = false;
}
