package com.example.onlinejudge.service.runner;

import com.example.onlinejudge.exception.CompilationException;
import com.example.onlinejudge.utils.OJConstants;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class CPPExecutionService implements ExecutionService {
    @Override
    public void compile(String codeContent, String fileName) throws Exception {
        try {
            String compileOutputPath = fileName.replace("cpp", "exe");
            ProcessBuilder processBuilder = new ProcessBuilder("g++", fileName, "-o", compileOutputPath);
            Process compileProcess = processBuilder.start();
            int compilationResult = compileProcess.waitFor();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
            StringBuilder errorMessage = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            if (compilationResult != 0) {
                throw new CompilationException(errorMessage.toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong: " + e.getMessage(), e);
        }
    }

    @Override
    public String execute(String codeContent, String input, String fileName) throws Exception {
        try {
            String executionPath = fileName.replace("cpp", "exe");
            ProcessBuilder processBuilder = new ProcessBuilder(executionPath);
            Process executionProcess = processBuilder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(executionProcess.getOutputStream()))) {
                writer.write(input.trim());
            }
            // Capture the standard output and standard error of the program
            String standardOutput;
            String standardError;
            try (InputStreamReader inputReader = new InputStreamReader(executionProcess.getInputStream());
                 InputStreamReader errorReader = new InputStreamReader(executionProcess.getErrorStream())) {
                // Read the program's standard output
                standardOutput = new BufferedReader(inputReader).lines().collect(Collectors.joining("\n"));
                // Read the program's standard error (if any)
                standardError = new BufferedReader(errorReader).lines().collect(Collectors.joining("\n"));
            }
            // Wait for the program to complete and get its exit code
            int exitCode = executionProcess.waitFor();

            // Check if the program executed successfully (exit code 0)
            if (exitCode != 0) {
                throw new RuntimeException("Execution failed with exit code: " + exitCode + "\nError:\n" + standardError);
            }
            return standardOutput;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new Exception("Execution failed for one or more test cases.");
        }
    }
}
