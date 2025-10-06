package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.decode.*;
import org.noear.snack4.codec.encode.*;
import org.noear.snack4.codec.factory.CollectionFactory;
import org.noear.snack4.codec.factory.ListFactory;
import org.noear.snack4.codec.factory.MapFactory;
import org.noear.snack4.codec.factory.SetFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 *
 * @author noear 2025/10/3 created
 */
public class CodecLib {
    private static CodecLib DEFAULT = new CodecLib(null).loadDefault();

    private final Map<Class<?>, ObjectFactory<?>> factorys = new HashMap<>();

    private final Map<Class<?>, ObjectDecoder<?>> decoders = new HashMap<>();
    private final List<ObjectPatternDecoder<?>> patternDecoders = new ArrayList<>();

    private final Map<Class<?>, ObjectEncoder<?>> encoders = new HashMap<>();
    private final List<ObjectPatternEncoder<?>> patternEncoders = new ArrayList<>();

    private final CodecLib parent;

    private CodecLib(CodecLib parent) {
        this.parent = parent;
    }

    public static CodecLib newInstance() {
        return new CodecLib(DEFAULT);
    }

    /**
     * 添加工厂
     */
    public <T> void addFactory(Class<T> type, ObjectFactory<T> factory) {
        factorys.put(type, factory);
    }

    /**
     * 添加解码器
     */
    public void addDecoder(ObjectPatternDecoder decoder) {
        patternDecoders.add(decoder);
    }

    /**
     * 添加解码器
     */
    public <T> void addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        if (decoder instanceof ObjectPatternDecoder<?>) {
            patternDecoders.add((ObjectPatternDecoder<?>) decoder);
        } else {
            decoders.put(type, decoder);
        }
    }

    /**
     * 添加编码器
     */
    public void addEncoder(ObjectPatternEncoder encoder) {
        patternEncoders.add(encoder);
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        if (encoder instanceof ObjectPatternEncoder) {
            patternEncoders.add((ObjectPatternEncoder<T>) encoder);
        } else {
            encoders.put(type, encoder);
        }
    }

    public ObjectDecoder getDecoder(Class<?> clazz) {
        ObjectDecoder decoder = decoders.get(clazz);

        if (decoder == null) {
            for (ObjectPatternDecoder decoder1 : patternDecoders) {
                if (decoder1.canDecode(clazz)) {
                    return decoder1;
                }
            }

            if (parent != null) {
                return parent.getDecoder(clazz);
            }
        }

        return decoder;
    }

    public ObjectFactory getFactory(Class<?> clazz) {
        ObjectFactory factory = factorys.get(clazz);

        if (factory == null) {
            if (parent != null) {
                return parent.getFactory(clazz);
            }
        }

        return factory;
    }

    public ObjectEncoder getEncoder(Object value) {
        ObjectEncoder encoder = encoders.get(value.getClass());

        if (encoder == null) {
            for (ObjectPatternEncoder encoder1 : patternEncoders) {
                if (encoder1.canEncode(value)) {
                    return encoder1;
                }
            }

            if (parent != null) {
                return parent.getEncoder(value);
            }
        }

        return encoder;
    }

    /// //////////////////////

    private void loadDefaultFactories() {
        addFactory(Map.class, new MapFactory());
        addFactory(List.class, new ListFactory());
        addFactory(Set.class, new SetFactory());
        addFactory(Collection.class, new CollectionFactory());
    }

    private void loadDefaultDecoders() {
        addDecoder(new _ArrayPatternDecoder());
        addDecoder(new _EnumPatternDecoder());

        addDecoder(StackTraceElement.class, new StackTraceElementDecoder());
        addDecoder(Properties.class, new PropertiesDecoder());
        addDecoder(InetSocketAddress.class, new InetSocketAddressDecoder());
        addDecoder(SimpleDateFormat.class, new SimpleDateFormatDecoder());
        addDecoder(File.class, new FileDecoder());
        addDecoder(TimeZone.class, new TimeZoneDecoder());
        addDecoder(UUID.class, new UUIDDecoder());

        addDecoder(URI.class, new URIDecoder());
        addDecoder(URL.class, new URLDecoder());

        addDecoder(String.class, new StringDecoder());

        addDecoder(Date.class, new DateDecoder());

        addDecoder(LocalTime.class, new LocalTimeDecoder());
        addDecoder(LocalDateTime.class, new LocalDateTimeDecoder());
        addDecoder(LocalDate.class, new LocalDateDecoder());

        addDecoder(OffsetDateTime.class, new OffsetDateTimeDecoder());
        addDecoder(OffsetTime.class, new OffsetTimeDecoder());

        addDecoder(ZonedDateTime.class, new ZonedDateTimeDecoder());

        addDecoder(java.sql.Date.class, new SqlDateDecoder());
        addDecoder(java.sql.Time.class, new SqlTimeDecoder());
        addDecoder(java.sql.Timestamp.class, new SqlTimestampDecoder());

        addDecoder(Boolean.class, new BooleanDecoder());
        addDecoder(Boolean.TYPE, new BooleanDecoder());

        addDecoder(BigDecimal.class, new BigDecimalDecoder());
        addDecoder(BigInteger.class, new BigIntegerDecoder());

        addDecoder(Double.class, new DoubleDecoder());
        addDecoder(Double.TYPE, new DoubleDecoder());

        addDecoder(Float.class, new FloatDecoder());
        addDecoder(Float.TYPE, new FloatDecoder());

        addDecoder(Long.class, new LongDecoder());
        addDecoder(Long.TYPE, new LongDecoder());

        addDecoder(Integer.class, new IntegerDecoder());
        addDecoder(Integer.TYPE, new IntegerDecoder());

        addDecoder(Short.class, new ShortDecoder());
        addDecoder(Short.TYPE, new ShortDecoder());
    }


    private void loadDefaultEncoders() {
        addEncoder(new _CalendarPatternEncoder());
        addEncoder(new _ClobPatternEncoder());
        addEncoder(new _DatePatternEncoder());
        addEncoder(new _EnumPatternEncoder());

        addEncoder(StackTraceElement.class, new StackTraceElementEncoder());
        addEncoder(ONode.class, new ONodeEncoder());
        addEncoder(Properties.class, new PropertiesEncoder());
        addEncoder(InetSocketAddress.class, new InetSocketAddressEncoder());
        addEncoder(SimpleDateFormat.class, new SimpleDateFormatEncoder());
        addEncoder(File.class, new FileEncoder());
        addEncoder(Calendar.class, new _CalendarPatternEncoder());
        addEncoder(Class.class, new ClassEncoder());
        addEncoder(Clob.class, new _ClobPatternEncoder());
        addEncoder(Currency.class, new CurrencyEncoder());
        addEncoder(TimeZone.class, new TimeZoneEncoder());
        addEncoder(UUID.class, new UUIDEncoder());

        addEncoder(URI.class, new URIEncoder());
        addEncoder(URL.class, new URLEncoder());

        addEncoder(String.class, new StringEncoder());


        addEncoder(LocalDateTime.class, new LocalDateTimeEncoder());
        addEncoder(LocalDate.class, new LocalDateEncoder());
        addEncoder(LocalTime.class, new LocalTimeEncoder());

        addEncoder(OffsetDateTime.class, new OffsetDateTimeEncoder());
        addEncoder(OffsetTime.class, new OffsetTimeEncoder());

        addEncoder(ZonedDateTime.class, new ZonedDateTimeEncoder());

        addEncoder(Boolean.class, new BooleanEncoder());
        addEncoder(Boolean.TYPE, new BooleanEncoder());

        addEncoder(Number.class, new _NumberPatternEncoder());
    }

    private CodecLib loadDefault() {
        loadDefaultDecoders();
        loadDefaultFactories();
        loadDefaultEncoders();
        return this;
    }
}