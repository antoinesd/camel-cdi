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
import org.apache.camel.impl.DefaultCamelBeanPostProcessor;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;

@Vetoed
final class CdiCamelBeanPostProcessor extends DefaultCamelBeanPostProcessor {

    private final BeanManager manager;
    
    private final CdiCamelExtension extension;

    CdiCamelBeanPostProcessor(BeanManager manager, CdiCamelExtension extension) {
        this.manager = manager;
        this.extension = extension;
    }

    @Override
    public CamelContext getOrLookupCamelContext() {
        return extension.getCamelContext(manager);
    }
}
