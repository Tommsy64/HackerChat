package io.github.tommsy64.hackerchat.arguments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.beust.jcommander.Parameter;

import lombok.Data;

@Data
public class HackerchatArguments {
    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = { "--version", "-version", "-v" }, description = "Prints out the version info", help = true)
    private boolean version = false;

    private String versionString = "unknown";

    public HackerchatArguments() {
        try {
            // Checks to see if running from a jar
            if (!this.getClass().getResource(this.getClass().getSimpleName() + ".class").toString().startsWith("jar"))
                return;
            Properties p = new Properties();
            InputStream is = this.getClass().getResourceAsStream("/META-INF/maven/io.github.tommsy64/hackerchat/pom.properties");
            p.load(is);
            this.versionString = p.getProperty("version");
        } catch (IOException e) {
            System.err.println("Error reading project properties: " + e.getLocalizedMessage());
        }
    }
}
