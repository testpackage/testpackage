/*
 * Copyright 2013 Deloitte Digital and Richard North
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.testpackage.example.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@SpringApplicationConfiguration(classes = {SpringTest.FunctionalTestConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringTest {

    @Value("${service.url}")
    private String serviceUrl;

    @Test
    public void someTest() {
        assertNotNull(serviceUrl);
        System.out.println("Service URL is not null - it is: " + serviceUrl);
    }


    @Configuration
    @ComponentScan
    @PropertySources({@PropertySource("classpath:/functionaltests.properties")})
    public static class FunctionalTestConfiguration {

        @Bean
        public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}