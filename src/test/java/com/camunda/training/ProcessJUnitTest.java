package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(ProcessEngineExtension.class)
public class ProcessJUnitTest {

    @Test
    @Deployment(resources = "c7-platform-java.bpmn")
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        // Start process with Java API and variables
        ProcessInstance processInstance = BpmnAwareTests.runtimeService()
                                                        .startProcessInstanceByKey("tweeter-qa", variables);

        assertThat(processInstance).isNotNull().isStarted()
                                  .task()
                                  .hasDefinitionKey("review_tweet")
                                  .hasCandidateGroup("management"); // Assuming "management" is a specific user ID or group name
        // Complete the user task to approve the tweet
        Task task = task(processInstance);
        runtimeService().setVariable(processInstance.getId(), "approved", true);

        BpmnAwareTests.taskService().complete(task.getId());

        // Make assertions on the process instance
        assertThat(processInstance).isEnded();

    }

}
