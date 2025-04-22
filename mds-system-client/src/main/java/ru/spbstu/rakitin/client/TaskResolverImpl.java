package ru.spbstu.rakitin.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskInformator;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;
import ru.spbstu.rakitin.requests.kafka.FindTopicByIdRequest;

import java.util.HashMap;
import java.util.Map;

public class TaskResolverImpl implements TaskResolver {

    private final MdsClient mdsClient;
    private final Map<TaskClientDto, String> topicNameCache = new HashMap<>();

    public TaskResolverImpl(MdsClient mdsClient) {
        this.mdsClient = mdsClient;
    }

    @Override
    public String resolveTopicName(TaskClientDto taskClientDto) {
        if (topicNameCache.containsKey(taskClientDto)) {
            return topicNameCache.get(taskClientDto);
        }

        TaskInformator taskInformator = resolveTaskInformation(taskClientDto);
        long topicId = taskInformator.getTopicId();
        MdsResponse<LightWeightTopicDto> topic = mdsClient.sendRequest(new FindTopicByIdRequest(topicId));
        String nameInKafka = topic.getResponse().get().getNameInKafka();
        topicNameCache.put(taskClientDto, nameInKafka);
        return nameInKafka;
    }

    @Override
    public TaskInformator resolveTaskInformation(TaskClientDto taskClientDto) {
        return getTaskInformatorFor(taskClientDto);
    }

    @Override
    public long resolveTaskId(TaskClientDto taskClientDto) {
        return getTaskInformatorFor(taskClientDto).getTaskId();
    }


    public TaskInformator getTaskInformatorFor(final TaskClientDto taskClientDto) {
        boolean requestId = false;
        if (taskClientDto.getTaskId() != null) {
            requestId = true;
        } else if (taskClientDto.getTaskName() == null || taskClientDto.getProjectId() == null) {
            throw new IllegalArgumentException("Task name or project id or task id is required");
        }
        boolean finalRequestId = requestId;
        TaskInformator taskInformator;
        if (taskClientDto.getTaskType() == TaskType.FULLTEXT) {
            MdsRequest<Void, FulltextTaskResponse> mdsRequest = getMdsRequest(taskClientDto, finalRequestId, FulltextTaskResponse.class);
            taskInformator = mdsClient.sendRequest(mdsRequest).getResponse().get();
        } else if (taskClientDto.getTaskType() == TaskType.ARCHIVE) {
            MdsRequest<Void, ArchiveTaskResponse> mdsRequest = getMdsRequest(taskClientDto, finalRequestId, ArchiveTaskResponse.class);
            taskInformator = mdsClient.sendRequest(mdsRequest).getResponse().get();
        } else if (taskClientDto.getTaskType() == TaskType.MONITORING) {
            MdsRequest<Void, MonitoringTaskResponse> mdsRequest = getMdsRequest(taskClientDto, finalRequestId, MonitoringTaskResponse.class);
            taskInformator = mdsClient.sendRequest(mdsRequest).getResponse().get();
        } else {
            throw new IllegalArgumentException("Task type not supported");
        }
        return taskInformator;
    }

    private <T extends TaskInformator> MdsRequest<Void, T> getMdsRequest(TaskClientDto taskClientDto, boolean finalRequestId, Class<T> clazz) {
        return new MdsRequest<>() {
            @Override
            public boolean hasResponse() {
                return true;
            }

            @Override
            public ParameterizedTypeReference<T> getResponseClass() {
                return new ParameterizedTypeReference<T>() {
                };
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
