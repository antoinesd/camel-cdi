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
package org.apache.camel.cdi.example1;

import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ContextName("simple")
public class SimpleCamelContext extends DefaultCamelContext {

    @PostConstruct
    void postConstruct() {
        super.setTracing(false);

        getComponent("properties", PropertiesComponent.class).setLocation("classpath:placeholder.properties");

        try {
            super.start();
        } catch (Exception cause) {
            cause.printStackTrace();
        }
    }

    @PreDestroy
    void preDestroy() {
        try {
            super.stop();
        } catch (Exception cause) {
            cause.printStackTrace();
        }
    }
}
