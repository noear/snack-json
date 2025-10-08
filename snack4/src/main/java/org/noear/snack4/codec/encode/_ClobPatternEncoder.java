package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;
import org.noear.snack4.SnackException;

import java.io.Reader;
import java.sql.Clob;

/**
 *
 * @author noear 2025/10/3 created
 *
 */
public class _ClobPatternEncoder implements ObjectPatternEncoder<Clob> {
    @Override
    public boolean canEncode(Object value) {
        return value instanceof Clob;
    }

    @Override
    public ONode encode(EncodeContext ctx, Clob value, ONode target) {
        return target.setValue(clobToString(value));
    }

    static String clobToString(Clob clob) {
        StringBuilder buf = new StringBuilder();

        try (Reader reader = clob.getCharacterStream()) {
            char[] chars = new char[1024];
            int size = 0;

            while ((size = reader.read(chars, 0, chars.length)) != -1) {
                buf.append(chars, 0, size);
            }
        } catch (Throwable e) {
            throw new SnackException("Clob read fail!", e);
        }

        return buf.toString();
    }
}