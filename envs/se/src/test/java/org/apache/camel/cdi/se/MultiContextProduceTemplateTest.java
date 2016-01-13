/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.cdi.se;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.CdiCamelExtension;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;
import org.apache.camel.cdi.se.bean.DefaultCamelContextBean;
import org.apache.camel.cdi.se.bean.FirstCamelContextBean;
import org.apache.camel.cdi.se.bean.FirstCamelContextProduceTemplateBean;
import org.apache.camel.cdi.se.bean.ProduceTemplateBean;
import org.apache.camel.cdi.se.bean.SecondCamelContextBean;
import org.apache.camel.cdi.se.bean.SecondCamelContextProduceTemplateBean;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.apache.camel.cdi.se.expression.ExchangeExpression.fromCamelContext;
import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

@RunWith(Arquillian.class)
public class MultiContextProduceTemplateTest {

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class)
            // Camel CDI
            .addPackage(CdiCamelExtension.class.getPackage())
            // Test classes
            .addClasses(
                DefaultCamelContextBean.class,
                ProduceTemplateBean.class,
                FirstCamelContextBean.class,
                FirstCamelContextProduceTemplateBean.class,
                SecondCamelContextBean.class,
                SecondCamelContextProduceTemplateBean.class)
            // Bean archive deployment descriptor
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private CamelContext defaultCamelContext;

    @Inject @Uri("direct:inbound")
    private ProducerTemplate defaultInbound;

    @Inject @Uri("mock:outbound")
    private MockEndpoint defaultOutbound;

    @Inject @ContextName("first")
    private CamelContext firstCamelContext;

    @Inject @ContextName("first") @Uri("direct:inbound")
    private ProducerTemplate firstInbound;

    @Inject @ContextName("first") @Uri("mock:outbound")
    private MockEndpoint firstOutbound;

    @Inject @ContextName("second")
    private CamelContext secondCamelContext;

    @Inject @ContextName("second") @Uri("direct:inbound")
    private ProducerTemplate secondInbound;

    @Inject @ContextName("second") @Uri("mock:outbound")
    private MockEndpoint secondOutbound;

    @Test
    @InSequence(1)
    public void configureCamelContexts() throws Exception {
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:inbound").bean(ProduceTemplateBean.class);
            }
        });

        firstCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:inbound").bean(FirstCamelContextProduceTemplateBean.class);
            }
        });

        secondCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:inbound").bean(SecondCamelContextProduceTemplateBean.class);
            }
        });

        secondCamelContext.startAllRoutes();
    }

    @Test
    @InSequence(2)
    public void sendMessageToDefaultCamelContextInbound() throws InterruptedException {
        defaultOutbound.expectedMessageCount(1);
        defaultOutbound.expectedBodiesReceived("test-processed");
        defaultOutbound.message(0).exchange().matches(fromCamelContext("camel-cdi"));

        defaultInbound.sendBody("test");

        assertIsSatisfied(2L, TimeUnit.SECONDS, defaultOutbound);
    }

    @Test
    @InSequence(3)
    public void sendMessageToFirstCamelContextInbound() throws InterruptedException {
        firstOutbound.expectedMessageCount(1);
        firstOutbound.expectedBodiesReceived("test-first");
        firstOutbound.message(0).exchange().matches(fromCamelContext("first"));

        firstInbound.sendBody("test");

        assertIsSatisfied(2L, TimeUnit.SECONDS, firstOutbound);
    }

    @Test
    @InSequence(4)
    public void sendMessageToSecondCamelContextInbound() throws InterruptedException {
        secondOutbound.expectedMessageCount(1);
        secondOutbound.expectedBodiesReceived("test-second");
        secondOutbound.message(0).exchange().matches(fromCamelContext("second"));

        secondInbound.sendBody("test");

        assertIsSatisfied(2L, TimeUnit.SECONDS, secondOutbound);
    }
}
