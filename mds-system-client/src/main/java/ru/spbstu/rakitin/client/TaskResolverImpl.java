package ru.spbstu.rakitin.client;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskInformator;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.requests.kafka.FindTopicByIdRequest;

import java.util.Map;

public class TaskResolverImpl implements TaskResolver {

    private final MdsClient mdsClient;

    public TaskResolverImpl(MdsClient mdsClient) {
        this.mdsClient = mdsClient;
    }

    @Override
    public String resolveTopicName(TaskClientDto taskClientDto) {
        MdsResponse<TaskInformator> taskInformatorMdsResponse = mdsClient.sendRequest(createRequestFor(taskClientDto));
        long topicId = taskInformatorMdsResponse.getResponse().get().getTopicId();
        MdsResponse<LightWeightTopicDto> topic = mdsClient.sendRequest(new FindTopicByIdRequest(topicId));
        return topic.getResponse().get().getNameInKafka();
    }


    public MdsRequest<Void, TaskInformator> createRequestFor(final TaskClientDto taskClientDto) {
        boolean requestId = false;
        if (taskClientDto.getTaskId() != null) {
            requestId = true;
        } else if (taskClientDto.getTaskName() == null || taskClientDto.getProjectId() == null) {
            throw new IllegalArgumentException("Task name or project id or task id is required");
        }
        boolean finalRequestId = requestId;
        return new MdsRequest<>() {
            @Override
            public boolean hasResponse() {
                return true;
            }

            @Override
            public boolean hasBody() {
                return false;
            }

            @Override
            public HttpMethod method() {
                return HttpMethod.GET;
            }

            @Override
            public MultiValueMap<String, String> getRequestParams() {
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                if (!finalRequestId) {
                    params.add("projectId", String.valueOf(taskClientDto.getProjectId()));
                }

                return params;
            }

            @Override
            public Map<String, Object> getUriVariables() {
                Map<String, Object> uriVariables = super.getUriVariables();
                if (finalRequestId) {
                    uriVariables.put("taskId", String.valueOf(taskClientDto.getTaskId()));
                } else {
                    uriVariables.put("taskName", taskClientDto.getTaskName());
                }
                return uriVariables;
            }

            @Override
            public String buildPath() {
                if (finalRequestId) {
                    return "/{taskId}";
                } else {
                    return "/name/{taskName}";
                }
            }

            @Override
            public ApiName apiName() {
                return resolveApiName(taskClientDto.getTaskType());
            }
        };
    }

    private ApiName resolveApiName(TaskType taskType) {
        if (taskType == TaskType.ARCHIVE) {
            return ApiName.ARCHIVE_CONFIG;
        } else if (taskType == TaskType.MONITORING) {
            return ApiName.MONITORING_CONFIG;
        } else if (taskType == TaskType.FULLTEXT) {
            return ApiName.FULLTEXT_CONFIG;
        }
        throw new IllegalArgumentException("Unresolvable task type: " + taskType);
    }


}
