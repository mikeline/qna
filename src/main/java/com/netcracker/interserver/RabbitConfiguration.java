package com.netcracker.interserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//Some queues are defined and bind in InterserverCommunication.java
@Configuration
//@ConfigurationProperties(prefix )
public class RabbitConfiguration {
    @Value("${broker.address}")
    private String RABBIT_ADDRESS;

    @Value("${broker.port}")
    private int RABBIT_PORT;

    @Value("${broker.vhost}")
    private String RABBIT_VHOST;

    @Value("${broker.username}")
    private String RABBIT_USERNAME;

    @Value("${broker.password}")
    private String RABBIT_PASSWORD;

    public static final String EXCHANGE_USER_AUTHENTICATION = "user auth"; //todo: make authentication
//    public static final String EXCHANGE_SEND_REPLICATION = "EXCHANGE_SEND_REPLICATION"; // upstream for EXCHANGE_REPLICATION
    public static final String EXCHANGE_REPLICATION = "EXCHANGE_REPLICATION"; // federated; connects to EXCHANGE_REPLICATION
    public static final String EXCHANGE_SEARCH = "EXCHANGE_SEARCH";


    public static final String QUEUE_RECEIVE_REPLICATION = "QUEUE_RECEIVE_REPLICATION";
    public static final String QUEUE_SEARCH = "QUEUE_SEARCH"; // federated
    public static final String QUEUE_SEARCH_QUERY = "QUEUE_SEARCH_QUERY";
    public static final String QUEUE_SEARCH_RESULT = "QUEUE_SEARCH_RESULT";


    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(rabbitTemplate());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate() {
        AsyncRabbitTemplate template = new AsyncRabbitTemplate(rabbitTemplate());
        return template;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(RABBIT_ADDRESS);
        connectionFactory.setVirtualHost(RABBIT_VHOST);
        connectionFactory.setPort(RABBIT_PORT);
        connectionFactory.setUsername(RABBIT_USERNAME);
        connectionFactory.setPassword(RABBIT_PASSWORD);

        return connectionFactory;
    }


    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(objectMapper(), "*");
//        DefaultClassMapper classMapper = new DefaultClassMapper();
//        classMapper.setTrustedPackages("*");
//        jsonConverter.setClassMapper(classMapper);
        jsonConverter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        return jsonConverter;
    }



    // exchanges
//    @Bean(EXCHANGE_SEND_REPLICATION)
//    public FanoutExchange exchangeSendReplication() {
//        return new FanoutExchange(EXCHANGE_SEND_REPLICATION);
//    }

    @Bean(EXCHANGE_REPLICATION)
    public TopicExchange exchangeReceiveReplication() {
        return new TopicExchange(EXCHANGE_REPLICATION);
    }

    @Bean(EXCHANGE_SEARCH)
    public TopicExchange exchangeSearch() {
        return new TopicExchange(EXCHANGE_SEARCH);
    }


    // queues
//    @Bean
//    @Scope("prototype")
//    @Primary
//    public Queue queue() {
//        return rabbitAdmin().declareQueue();
//    }

    @Bean
    public Queue queueReceiveReplication() {
        return new Queue(QUEUE_RECEIVE_REPLICATION);
    }

    @Bean
    public Queue queueSearch() {
        return new Queue(QUEUE_SEARCH);
    }

    @Bean
    public Queue queueSearchQuery() {
        return new Queue(QUEUE_SEARCH_QUERY);
    }

    @Bean(QUEUE_SEARCH_RESULT)
    public Queue queueSearchResult() {
        return new Queue(QUEUE_SEARCH_RESULT);
    }
//
//    @Bean
//    public Queue queueReplySummary() {
//        return new Queue(QUEUE_REPLY_SUMMARY);
//    }

//    //bindings
//    @Bean
//    public Binding bindRequestResume() {
//        return BindingBuilder
//                .bind(queueRequestSummary())
//                .to(exchangeRequestSummary());
//    }

//    @Bean
//    public Binding bindReceiveReplication() {
//        return BindingBuilder.bind(queueReceiveReplication()).to(exchangeReceiveReplication());
//    }

}
