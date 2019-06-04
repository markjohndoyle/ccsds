/*
 *   Copyright (c) 2019 Dario Lucia (https://www.dariolucia.eu)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package eu.dariolucia.ccsds.encdec.structure;

import eu.dariolucia.ccsds.encdec.definition.EncodedParameter;
import eu.dariolucia.ccsds.encdec.definition.DataTypeEnum;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DecodingResult {

    private final List<Item> decodedItems;
    private final List<ParameterValue> decodedParameters;

    public DecodingResult(List<Item> decodedItems, List<ParameterValue> decodedParameters) {
        this.decodedItems = List.copyOf(decodedItems);
        this.decodedParameters = List.copyOf(decodedParameters);
    }

    public List<Item> getDecodedItems() {
        return decodedItems;
    }

    public List<ParameterValue> getDecodedParameters() {
        return decodedParameters;
    }

    public Map<String, Object> getDecodedItemsAsMap() {
        final Map<String, Object> map = new TreeMap<>();
        for(Item i : decodedItems) {
            i.visit(new IVisitor() {
                @Override
                public void visitParameter(Parameter p) {
                    map.put(p.location.toString(), p.value);
                }
            });
        }
        return map;
    }

    public void visit(IVisitor v) {
        for(Item i : decodedItems) {
            i.visit(v);
        }
    }

    public interface IVisitor {
        default void visitParameter(Parameter p) {}
        default void visitArrayStart(Array a) {}
        default void visitArrayItemStart(Array a, int idx) {}
        default void visitArrayItemEnd(Array a, int idx) {}
        default void visitArrayEnd(Array a) {}
        default void visitStructureStart(Structure a) {}
        default void visitStructureItemStart(Structure a, int idx) {}
        default void visitStructureItemEnd(Structure a, int idx) {}
        default void visitStructureEnd(Structure a) {}
    }

    public static abstract class Item {
        public final PathLocation location;
        public final String name;

        public Item(PathLocation location, String name) {
            this.location = location;
            this.name = name;
        }

        public abstract void visit(IVisitor v);
    }

    public static class Parameter extends Item {

        public final EncodedParameter parameter;
        public final DataTypeEnum actualType;
        public final Object value;
        public final Instant generationTime;

        public Parameter(PathLocation location, String name, EncodedParameter parameter, DataTypeEnum actualType, Object value) {
            this(location, name, parameter, actualType, value, null);
        }

        public Parameter(PathLocation location, String name, EncodedParameter parameter, DataTypeEnum actualType, Object value, Instant generationTime) {
            super(location, name);
            this.parameter = parameter;
            this.actualType = actualType;
            this.value = value;
            this.generationTime = generationTime;
        }

        @Override
        public void visit(IVisitor v) {
            v.visitParameter(this);
        }
    }

    public static class Array extends Item {
        public final List<ArrayItem> array;

        public Array(PathLocation location, String name, List<ArrayItem> array) {
            super(location, name);
            this.array = array;
        }

        @Override
        public void visit(IVisitor v) {
            v.visitArrayStart(this);
            for(int i = 0; i < array.size(); ++i) {
                v.visitArrayItemStart(this, i);
                array.get(i).visit(v);
                v.visitArrayItemEnd(this, i);
            }
            v.visitArrayEnd(this);
        }
    }

    public static class ArrayItem extends Item {
        public final List<Item> array;

        public ArrayItem(PathLocation location, String name, List<Item> array) {
            super(location, name);
            this.array = array;
        }

        @Override
        public void visit(IVisitor v) {
            for(int i = 0; i < array.size(); ++i) {
                array.get(i).visit(v);
            }
        }
    }

    public static class Structure extends Item {
        public final List<Item> properties;

        public Structure(PathLocation location, String name, List<Item> properties) {
            super(location, name);
            this.properties = properties;
        }

        @Override
        public void visit(IVisitor v) {
            v.visitStructureStart(this);
            for(int i = 0; i < properties.size(); ++i) {
                v.visitStructureItemStart(this, i);
                properties.get(i).visit(v);
                v.visitStructureItemEnd(this, i);
            }
            v.visitStructureEnd(this);
        }
    }
}
