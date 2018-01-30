package xyz.vimtool.commons;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * XML工具类
 *
 * @author   qinxiaoqing
 * @version  1.0
 * @date     2017/08/24
 */
public class XMLUtils {

    /**
     * 用于转换Map的转换器
     */
    private static class MapExtConverter implements Converter {

        @Override
        public boolean canConvert(Class clazz) {
            return Map.class.isAssignableFrom(clazz);
        }

        @Override
        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            Map map = (Map) value;
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode(entry.getKey().toString());
                writer.setValue(entry.getValue().toString());
                writer.endNode();
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Map<String, String> map = new HashMap<>();
            while(reader.hasMoreChildren()) {
                reader.moveDown();
                map.put(reader.getNodeName(), reader.getValue());
                reader.moveUp();
            }
            return map;
        }
    }

    /**
     * 初始化XStream
     *
     * @param cdata 是否支持CDATA标签
     */
    private static XStream initXStream(boolean cdata){
        if (!cdata) {
            return new XStream(new DomDriver("UTF-8"));
        }

        return new XStream(new DomDriver("UTF-8", new NoNameCoder()) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    @Override
                    public String encodeNode(String name) {
                        return name;
                    }

                    @Override
                    protected void writeText(QuickWriter writer, String text) {
                        writer.write("<![CDATA[" + text + "]]>");
                    }
                };
            }
        });
    }

    /**
     * 对象转换为XML
     *
     * @param object 转换源对象
     * @param cdata  是否支持CDATA标签
     */
    public static String toXML(Object object, boolean cdata) {
        XStream xStream = initXStream(cdata);

        if (object instanceof Map) {
            xStream.alias("xml", Map.class);
            xStream.alias("xml", object.getClass());
            xStream.registerConverter(new MapExtConverter());
        } else {
            xStream.alias("xml", object.getClass());
        }

        return xStream.toXML(object);
    }

    /**
     * XML转换为对象
     *
     * @param xml    转换源
     * @param object 转换目标对象
     * @param cdata  是否支持CDATA标签
     */
    public static void fromXML(String xml, Object object, boolean cdata) {
        XStream xStream = initXStream(cdata);

        if (object instanceof Map) {
            xStream.alias("xml", Map.class);
            xStream.alias("xml", object.getClass());
            xStream.registerConverter(new MapExtConverter());
            ((Map) object).putAll((Map) xStream.fromXML(xml));
            return;
        } else {
            xStream.alias("xml", object.getClass());
            xStream.fromXML(xml, object);
        }
    }
}
