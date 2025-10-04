package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.decode.*;
import org.noear.snack4.codec.encode.*;
import org.noear.snack4.codec.factory.CollectionFactory;
import org.noear.snack4.codec.factory.ListFactory;
import org.noear.snack4.codec.factory.MapFactory;
import org.noear.snack4.codec.factory.SetFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 *
 * @author noear 2025/10/3 created
 */
public class CodecRepository {
    private static final Map<Class<?>, NodeDecoder<?>> DECODERS = new HashMap<>();
    private static final Map<Class<?>, ObjectFactory<?>> FACTORYS = new HashMap<>();

    private static final Map<Class<?>, NodeEncoder<?>> ENCODERS = new HashMap<>();
    private static final List<NodePatternEncoder<?>> PATTERN_ENCODERS = new ArrayList<>();

    public static <T> void add(Class<T> type, NodeDecoder<T> decoder) {
        DECODERS.put(type, decoder);
    }

    public static <T> void add(Class<T> type, ObjectFactory<T> factory) {
        FACTORYS.put(type, factory);
    }

    public static <T> void add(Class<T> type, NodeEncoder<T> encoder) {
        if (encoder instanceof NodePatternEncoder) {
            PATTERN_ENCODERS.add((NodePatternEncoder<T>) encoder);
        }
        ENCODERS.put(type, encoder);
    }

    public static NodeDecoder getNodeDecoder(Options opts, Class<?> clazz) {
        // 优先使用自定义编解码器
        NodeDecoder decoder = opts.getNodeDecoder(clazz);
        if (decoder != null) {
            return decoder;
        }

        //如果没有，用默认解码库
        return DECODERS.get(clazz);
    }

    public static ObjectFactory getObjectFactory(Options opts, Class<?> clazz) {
        ObjectFactory factory = opts.getObjectFactory(clazz);
        if (factory != null) {
            return factory;
        }

        return FACTORYS.get(clazz);
    }

    public static NodeEncoder getNodeEncoder(Options opts, Class<?> clazz) {
        NodeEncoder encoder = opts.getNodeEncoder(clazz);
        if (encoder == null) {
            encoder = ENCODERS.get(clazz);
        }

        if (encoder == null) {
            for (NodePatternEncoder encoder1 : PATTERN_ENCODERS) {
                if (encoder1.canEncode(clazz)) {
                    return encoder1;
                }
            }
        }

        return encoder;
    }

    private static void initDecoders() {
        add(Properties.class, new PropertiesDecoder());
        add(InetSocketAddress.class, new InetSocketAddressDecoder());
        add(SimpleDateFormat.class, new SimpleDateFormatDecoder());
        add(File.class, new FileDecoder());
        add(UUID.class, new UUIDDecoder());

        add(String.class, new StringDecoder());
        add(Date.class, new DateDecoder());

        add(Boolean.class, new BooleanDecoder());
        add(Boolean.TYPE, new BooleanDecoder());

        add(Double.class, new DoubleDecoder());
        add(Double.TYPE, new DoubleDecoder());

        add(Float.class, new FloatDecoder());
        add(Float.TYPE, new FloatDecoder());

        add(Long.class, new LongDecoder());
        add(Long.TYPE, new LongDecoder());

        add(Integer.class, new IntegerDecoder());
        add(Integer.TYPE, new IntegerDecoder());

        add(Short.class, new ShortDecoder());
        add(Short.TYPE, new ShortDecoder());
    }

    private static void initFactories() {
        add(Map.class, new MapFactory());
        add(List.class, new ListFactory());
        add(Set.class, new SetFactory());
        add(Collection.class, new CollectionFactory());
    }

    private static void initEncoders() {
        add(Calendar.class, new CalendarPatternEncoder());
        add(Clob.class, new ClobPatternEncoder());

        add(ONode.class, new ONodeEncoder());
        add(Properties.class, new PropertiesEncoder());
        add(InetSocketAddress.class, new InetSocketAddressEncoder());
        add(SimpleDateFormat.class, new SimpleDateFormatEncoder());
        add(File.class, new FileEncoder());
        add(Calendar.class, new CalendarPatternEncoder());
        add(Class.class, new ClassEncoder());
        add(Clob.class, new ClobPatternEncoder());
        add(Currency.class, new CurrencyEncoder());
        add(TimeZone.class, new TimeZoneEncoder());
        add(UUID.class, new UUIDEncoder());

        add(String.class, new StringEncoder());

        add(Date.class, new DateEncoder());
        add(ZonedDateTime.class, new ZonedDateTimeEncoder());

        add(OffsetDateTime.class, new OffsetDateTimeEncoder());
        add(OffsetTime.class, new OffsetTimeEncoder());

        add(LocalDateTime.class, new LocalDateTimeEncoder());
        add(LocalDate.class, new LocalDateEncoder());
        add(LocalTime.class, new LocalTimeEncoder());


        add(Boolean.class, new BooleanEncoder());
        add(Boolean.TYPE, new BooleanEncoder());

        add(Double.class, new DoubleEncoder());
        add(Double.TYPE, new DoubleEncoder());

        add(Float.class, new FloatEncoder());
        add(Float.TYPE, new FloatEncoder());

        add(Long.class, new LongEncoder());
        add(Long.TYPE, new LongEncoder());

        add(Integer.class, new IntegerEncoder());
        add(Integer.TYPE, new IntegerEncoder());

        add(Short.class, new ShortEncoder());
        add(Short.TYPE, new ShortEncoder());
    }

    static {
        initDecoders();
        initFactories();
        initEncoders();
    }
}
