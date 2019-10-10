package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.brillio.tms.kafka.KafkaTopicConfig.TOPIC;

@Service
public class AssignServiceCounterService implements IAssignServiceCounterService {

    private final ServiceCounterRegistry serviceCounterRegistry;
    public final static String topic = "SC1";
//    @Inject
    @Autowired
    public AssignServiceCounterService(ServiceCounterRegistry serviceCounterRegistry) {
        this.serviceCounterRegistry = serviceCounterRegistry;
    }

    @Override
    public AssignedToken assignToken(Token token) {
        return serviceCounterRegistry.assignServiceCounter(token);
    }

    @KafkaListener(topics = TOPIC, groupId = "brillio")
    public void listen(String message) {
        System.out.println("Received Messasge in group foo: " + message);
    }
}
