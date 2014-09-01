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
package org.apache.camel.cdi;

import org.apache.camel.component.properties.DefaultPropertiesParser;
import org.apache.camel.component.properties.PropertiesComponent;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Properties;

@Named("properties")
@Typed(PropertiesComponent.class)
class CdiPropertiesComponent extends PropertiesComponent {

    @Inject
    CdiPropertiesComponent(@Config Instance<Properties> properties) {
        setPropertiesParser(properties.isUnsatisfied() ? new DefaultPropertiesParser() : new CdiPropertiesParser(properties.get()));
    }
}