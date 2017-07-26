package io.wizzie.ks.normalizer.funcs.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wizzie.ks.normalizer.builder.StreamBuilder;
import io.wizzie.ks.normalizer.builder.config.Config;
import io.wizzie.ks.normalizer.exceptions.PlanBuilderException;
import io.wizzie.ks.normalizer.funcs.Function;
import io.wizzie.ks.normalizer.funcs.MapperFunction;
import io.wizzie.ks.normalizer.model.PlanModel;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TimeMapperUnitTest {

    static Config config = new Config();

    static {
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "app-id-1");
    }

    private static StreamBuilder streamBuilder = new StreamBuilder(config, null);

    @BeforeClass
    public static void initTest() throws IOException, PlanBuilderException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-millis.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder.builder(model);
    }

    @Test
    public void building() {
        Map<String, Function> functions = streamBuilder.getFunctions("stream1");
        Function myFunc = functions.get("myTimeMapper");

        assertNotNull(myFunc);
        assertTrue(myFunc instanceof MapperFunction);
        TimeMapper myTimeMapper = (TimeMapper) myFunc;


        assertEquals("timestamp", myTimeMapper.dimensionToProcess);
        assertEquals("millis", myTimeMapper.toFormat);
        assertEquals("secs", myTimeMapper.fromFormat);


    }

    @Test
    public void processSecsToMillisReadedFromConfig() {
        Map<String, Function> functions = streamBuilder.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890000L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);

        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);

    }

    @Test
    public void processSecsAsIntegerToMillisReadedFromConfig() {
        Map<String, Function> functions = streamBuilder.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890000L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);

        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);

    }

    @Test
    public void processSecsAsStringToMillisReadedFromConfig() {
        Map<String, Function> functions = streamBuilder.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890000L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);

        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);

    }


    @Test
    public void processSecsToISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-13T23:31:30.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);

    }

    @Test
    public void processSecsAsIntegerToISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-13T23:31:30.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);

    }

    @Test
    public void processSecsAsStringoISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-13T23:31:30.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }


    @Test
    public void processMillisToSecsReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-secs.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890000L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processMillisAsStringToSecsReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-secs.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890000");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processMillisAsStringToSecsAsStringReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-secs-string.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890000");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "1234567890");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processMillisToISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890000L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-13T23:31:30.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processMillisAsStringToISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890000");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-13T23:31:30.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }


    @Test
    public void processISOToSecsReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-ISO-to-secs.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "2009-02-13T23:31:30.000Z");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }


    @Test
    public void processISOToMillisReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-ISO-to-millis.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "2009-02-13T23:31:30.000Z");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234567890000L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processISOToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-ISO-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "2009-02-13T23:31:30.000Z");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);


    }

    @Test
    public void processMillisToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890000L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processMillisAsStringToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-millis-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890000");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processSecsToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890L);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processSecsAsIntegerToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", 1234567890);
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processSecsAsStringToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-secs-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "1234567890");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090213");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processPatternToPatternReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-pattern-to-pattern.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "2009-02-14");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "20090214");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processPatternToMillisReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-pattern-to-millis.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "20090214");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234569600000L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processPatternToSecsReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-pattern-to-secs.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "20090214");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", 1234569600L);
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processPatternToISOReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-pattern-to-ISO.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        streamBuilder2.builder(model);
        Map<String, Function> functions = streamBuilder2.getFunctions("stream1");
        Function myTimeFunc = functions.get("myTimeMapper");

        assertNotNull(myTimeFunc);
        assertTrue(myTimeFunc instanceof MapperFunction);
        MapperFunction myTimeMapper = (MapperFunction) myTimeFunc;

        Map<String, Object> message1 = new HashMap<>();
        message1.put("timestamp", "20090214");
        message1.put("dimension1", "value1");
        message1.put("dimension2", "value2");
        message1.put("dimension3", "value3");


        Map<String, Object> expectedMessage1 = new HashMap<>();
        expectedMessage1.put("timestamp", "2009-02-14T00:00:00.000Z");
        expectedMessage1.put("dimension1", "value1");
        expectedMessage1.put("dimension2", "value2");
        expectedMessage1.put("dimension3", "value3");

        KeyValue<String, Map<String, Object>> result1 = myTimeMapper.process("KEY", message1);
        streamBuilder2.close();
        assertEquals(new KeyValue<>("KEY", expectedMessage1), result1);
    }

    @Test
    public void processBadPropertiesReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-bad-properties.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        try {
            streamBuilder2.builder(model);
            fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processNullPropertiesReadedFromConfig() throws IOException, PlanBuilderException {

        StreamBuilder streamBuilder2 = new StreamBuilder(config, null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("time-mapper-null-properties.json").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        PlanModel model = objectMapper.readValue(file, PlanModel.class);
        try {
            streamBuilder2.builder(model);
            fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    @AfterClass
    public static void stop() {
        streamBuilder.close();
    }

}


