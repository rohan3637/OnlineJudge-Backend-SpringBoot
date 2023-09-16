package com.example.onlinejudge.service.runner;

import com.example.onlinejudge.exception.BadRequestException;
import com.example.onlinejudge.utils.OJConstants;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.stream.Collectors;

import static com.example.onlinejudge.utils.OJConstants.NODE_PATH;

@Service
@Component
public class JSExecutionService implements ExecutionService {

    @Override
    public void compile(String codeContent, String fileName) {
        return;
    }

    @Override
    public String execute(String codeContent, String input, String fileName) throws Exception {
        try {
            Process executionProcess;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(OJConstants.NODE_PATH, fileName);
                executionProcess = processBuilder.start();
            } catch (Exception ex) {
                throw new BadRequestException(ex.getMessage());
            }

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
            if (exitCode != 0) {
                throw new RuntimeException("Execution failed with exit code: " + exitCode + "\nError:\n" + standardError.toString());
            }
            return standardOutput.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new Exception("Execution failed for one or more test cases.");
        }
    }

}
