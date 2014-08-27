/**
 * Copyright (C) 2014 Antonin Stefanutti (antonin.stefanutti@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.astefanutti.camel.cdi;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.util.ObjectHelper;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@ApplicationScoped
@Typed({CamelContext.class, ModelCamelContext.class})
class CdiCamelContext extends DefaultCamelContext {

    CdiCamelContext() {
    }
    
    @Inject
    private CdiCamelContext(BeanManager beanManager) {
        super(new CdiBeanRegistry(beanManager));
        setInjector(new CdiInjector(getInjector(), beanManager));
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception cause) {
            throw ObjectHelper.wrapRuntimeCamelException(cause);
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        try {
            super.stop();
        } catch (Exception cause) {
            throw ObjectHelper.wrapRuntimeCamelException(cause);
        }
    }
}
