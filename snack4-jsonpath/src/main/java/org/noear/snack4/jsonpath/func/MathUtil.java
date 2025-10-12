package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/10/12 created
 *
 */
public class MathUtil {
    public static List<Double> getDoubleList(List<ONode> oNodes) {
        List<Double> list = new ArrayList<>();

        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        list.add(o.getDouble());
                    }
                }
            } else if (n.isNumber()) {
                list.add(n.getDouble());
            }
        }

        return list;
    }

    /**
     * 计算数组的平均值 (Mean)
     */
    public static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double num : data) {
            sum += num;
        }
        return sum / data.size();
    }

    /**
     * 计算数组的标准差 (Standard Deviation)
     * 使用总体 (Population) 标准差公式。
     */
    public static double calculateStdDev(List<Double> data) {
        if (data == null || data.size() == 0) {
            return Double.NaN; // 处理空数组或 null
        }

        // 步骤 1: 计算平均值 (Mean)
        double mean = calculateMean(data);

        // 步骤 2: 计算方差 (Variance) - 每个元素与平均值差的平方和
        double sumOfSquaredDifferences = 0.0;
        for (double num : data) {
            // (x_i - μ)^2
            sumOfSquaredDifferences += Math.pow(num - mean, 2);
        }

        // 步骤 3: 计算方差 (Variance) - 除以元素个数 N
        double variance = sumOfSquaredDifferences / data.size();

        // 步骤 4: 计算标准差 (StdDev) - 方差的平方根
        return Math.sqrt(variance);
    }

    public static Double sum(List<ONode> oNodes){
        double ref = 0D;
        int count = 0;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        ref += o.getDouble();
                        count++;
                    }
                }
            } else if (n.isNumber()) {
                ref += n.getDouble();
                count++;
            }
        }

        if (count > 0) {
            return ref;
        } else {
            return null;
        }
    }

    public static Double avg(List<ONode> oNodes) {
        double ref = 0D;
        int count = 0;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        ref += o.getDouble();
                        count++;
                    }
                }
            } else if (n.isNumber()) {
                ref += n.getDouble();
                count++;
            }
        }

        if (count > 0) {
            return ref / count;
        } else {
            return null;
        }
    }

    public static Double avgByChild(List<ONode> oNodes) {
        double ref = 0D;
        int count = 0;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        ref += o.getDouble();
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            return ref / count;
        } else {
            return null;
        }
    }
}
