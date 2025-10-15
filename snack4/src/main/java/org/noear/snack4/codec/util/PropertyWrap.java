/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.codec.util;


/**
 *
 * @author noear 2025/10/8 created
 * @since 4.0
 */
public class PropertyWrap {
    private final String name;
    private String nodeName;

    private FieldWrap fieldWrap;
    private PropertyMethodWrap getterWrap;
    private PropertyMethodWrap setterWrap;

    public PropertyWrap(String name) {
        this.name = name;
    }

    public String getOrigName() {
        return name;
    }

    public String getNodeName() {
        return nodeName;
    }

    public FieldWrap getFieldWrap() {
        return fieldWrap;
    }

    public PropertyMethodWrap getGetterWrap() {
        return getterWrap;
    }

    public PropertyMethodWrap getSetterWrap() {
        return setterWrap;
    }

    /// //////////

    protected void setFieldWrap(FieldWrap f) {
        this.fieldWrap = f;
        this.nodeName = f.getNodeName();
    }

    protected void setGetterWrap(PropertyMethodWrap g) {
        this.getterWrap = g;
        this.nodeName = g.getNodeName();
    }

    protected void setSetterWrap(PropertyMethodWrap s) {
        this.setterWrap = s;
        this.nodeName = s.getNodeName();
    }
}