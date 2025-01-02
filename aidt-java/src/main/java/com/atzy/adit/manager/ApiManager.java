package com.atzy.adit.manager;

import com.atzy.adit.common.ErrorCode;
import com.atzy.adit.exception.BusinessException;
import com.atzy.adit.exception.ThrowUtils;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: API请求模块管理封装
 */
@Component
public class ApiManager {
    @Resource
    private ClientV4 client;
    //稳定的随机数生成
    private static final float STABLE_TEMPERATURE = 0.05f;
    //不稳定的随机数生成
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * @description: 不稳定的随机数生成请求
     * @param: systemMessage userMessage
     */

    public String doUnStableSyncRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }

    /**
     * @description: 稳定的随机数生成请求
     * @param: systemMessage userMessage
     */
    public String doStableSyncRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    /**
     * @description: 同步方达请求
     * @param: systemMessage userMessage stream temperature
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * @description: 通用的请求简化方法
     * @param: systemMessage userMessage stream temperature
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.USER.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);
        return doRequest(messages, stream, temperature);
    }

    /**
     * @description: 通用的请求封装方法
     * @param: messages stream temperature
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {
        //构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            //调用模型接口
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

}
