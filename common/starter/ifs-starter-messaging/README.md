ifs-messaging-starter
=

This is a light wrapper around the spring-amqp starter mostly based around obtaining configuration 
from k8s or using sensible defaults for IDE use.

It is also used as a basis for configuring other starters that use messaging such as ifs-starter-audit

Usage
=

### Gradle
Services will need to import the starter -:

    implementation project(':common:starter:ifs-starter-messaging')

This in turn exports the following as API (inherited dependencies) -:
    
    api project(":common:api:messaging-api")    
    api "org.springframework.amqp:spring-amqp"
    api "org.springframework.amqp:spring-rabbit"

When importing classes into code default to the spring amqp classes over the implementation specific classes.

**The exception to this rule is @RabbitListener**

Profile
=

    SPRING_PROFILES_ACTIVE will need to contain an AMQP entry

**I'm planning to switch the default to AMQP being on at a later date and disabling it will require a profile to be set.**

Spring IDE mode
=
This will use the default config 

### K8s
In k8s the deployment will need to have rabbitMQ:rabbitMQ as an annotation.

Kustomize will then patch the deployment with the rabbit config and secrets for all environment you work in

    apiVersion: apps/v1
    kind: Deployment
    metadata:
        name: foo-service
        annotations:
            rabbitMQ: rabbitMQ


Opinionated Example
=
This works quite nicely with spring and covers most of our use cases where we are replacing a long running rest call with some sort of non-blocking task queue for an underlying service.

It's typesafe, uses mostly defaults, has reasonable QOS guarantees and is easy to maintain, run and test in a variety of scenarios.

### messaging-api
Nothing much here in here except CommonQueues

It is entirely optional as it's just a coding nicety but adding queue names in here will be useful for ongoing maintenance.
In addition, typos in the @Queue and @RabbitListener annotations are hard to spot so this prevents errors like this and makes it easy to track which services bind to which in IDEs.

### sending-messages
There are literally hundreds of ways to do this but this one allows typed objects to be sent and received minimising potential for marshalling errors. Obviously the class needs to be shared via an 'api' level package.

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void someMethod() {
        amqpTemplate.convertAndSend(CommonQueues.FOO, new WhatverYouLike());
    }

### receiving-messages
The receiving service in this case is responsible for declaring the queue. To maintain the ability for services to start without RabbitMQ running we wrap the configuration in the AMQP profile.

    @Configuration
    public class YourMainServiceConfiguration {

        @Configuration
        @Profile(IfsProfileConstants.AMQP_PROFILE)
        public static class YourServiceAmqpConfiguration {
    
            @Bean
            public ExampleMessageListener exampleMessageListener() {
                return new ExampleMessageListener();
            }
    
            @Bean
            public Queue uploadMessageQueue() {
                // Bind a default non-persistent queue FOO
                return new Queue(CommonQueues.FOO, false);
            }
        }
    }

We can then add a listener to pick messages off of the queue

    public class ExampleMessageListener {

        @RabbitListener(queues = {CommonQueues.FOO})
        public void fileUpload(WhatverYouLike whatverYouLike) {
            service.doWhatever(whatverYouLike);
        }
    }

### Patterns

Quite often the queue is an alternative way of calling an existing rest POST endpoint just as a different transport.

In the same way as @Controllers are set up, create a messaging package and add the various classes and listeners in there.

    ┌────────────┐  ┌────────────────┐
    │ AMQP       ├──┤@RabbitListener ├─────┐   ┌──────────┐
    └────────────┘  └────────────────┘     ├───┤ @Service |
    ┌────────────┐  ┌────────────────┐     |   └──────────┘
    │ REST       ├──┤@PostRequest    ├─────┘
    └────────────┘  └────────────────┘
