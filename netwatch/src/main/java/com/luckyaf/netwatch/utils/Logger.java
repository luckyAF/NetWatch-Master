package com.luckyaf.netwatch.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/16
 */

@SuppressWarnings("unused")
public final class Logger {
//    Logger.Builder builder = new Logger.Builder()
//            .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
//            .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
//            .setGlobalTag(null)// 设置log全局标签，默认为空
//            // 当全局标签不为空时，我们输出的log全部为该tag，
//            // 为空时，如果传入的tag为空那就显示类名，否则显示tag
//            .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
//            .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
//            .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
//            .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
//            .setConsoleFilter(Logger.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
//            .setFileFilter(Logger.V);// log文件过滤器，和logcat过滤器同理，默认Verbose

    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    private @interface TYPE {
    }

    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};

    private static final int FILE = 0x10;
    private static final int JSON = 0x20;
    private static final int XML = 0x30;
    private static ExecutorService executor;
    private static String defaultDir;// log默认存储目录
    private static String dir;       // log存储目录

    private static boolean sLogSwitch = true; // log总开关，默认开
    private static boolean sLog2ConsoleSwitch = true; // logcat是否打印，默认打印
    private static String sGlobalTag = null; // log标签
    private static boolean sTagIsSpace = true; // log标签是否为空白
    private static boolean sLogHeadSwitch = true; // log头部开关，默认开
    private static boolean sLog2FileSwitch = false;// log写入文件开关，默认关
    private static boolean sLogBorderSwitch = true; // log边框开关，默认开
    private static int sConsoleFilter = V;    // log控制台过滤器
    private static int sFileFilter = V;    // log文件过滤器

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_BORDER = "╔═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER = "╚═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final int MAX_LEN = 4000;
    private static final Format FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault());

    private static final String NULL_TIPS = "Log with null object.";
    private static final String NULL = "null";
    private static final String ARGS = "args";

    private Logger() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static class Builder {
        public Builder(Context context) {
            if (defaultDir != null) {
                return;
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && context.getExternalCacheDir() != null) {
                defaultDir = context.getExternalCacheDir() + FILE_SEP + "log" + FILE_SEP;
            } else {
                defaultDir = context.getCacheDir() + FILE_SEP + "log" + FILE_SEP;
            }
        }

        public Builder setLogSwitch(boolean logSwitch) {
            Logger.sLogSwitch = logSwitch;
            return this;
        }

        public Builder setConsoleSwitch(boolean consoleSwitch) {
            Logger.sLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        public Builder setGlobalTag(final String tag) {
            if (isSpace(tag)) {
                Logger.sGlobalTag = "";
                sTagIsSpace = true;
            } else {
                Logger.sGlobalTag = tag;
                sTagIsSpace = false;
            }
            return this;
        }

        public Builder setLogHeadSwitch(boolean logHeadSwitch) {
            Logger.sLogHeadSwitch = logHeadSwitch;
            return this;
        }

        public Builder setLog2FileSwitch(boolean log2FileSwitch) {
            Logger.sLog2FileSwitch = log2FileSwitch;
            return this;
        }

        public Builder setDir(final String dir) {
            if (isSpace(dir)) {
                Logger.dir = null;
            } else {
                Logger.dir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
            }
            return this;
        }

        public Builder setDir(final File dir) {
            Logger.dir = dir == null ? null : dir.getAbsolutePath() + FILE_SEP;
            return this;
        }

        public Builder setBorderSwitch(boolean borderSwitch) {
            Logger.sLogBorderSwitch = borderSwitch;
            return this;
        }

        public Builder setConsoleFilter(@TYPE int consoleFilter) {
            Logger.sConsoleFilter = consoleFilter;
            return this;
        }

        public Builder setFileFilter(@TYPE int fileFilter) {
            Logger.sFileFilter = fileFilter;
            return this;
        }

        @Override
        public String toString() {
            return "switch: " + sLogSwitch
                    + LINE_SEP + "console: " + sLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (sTagIsSpace ? "null" : sGlobalTag)
                    + LINE_SEP + "head: " + sLogHeadSwitch
                    + LINE_SEP + "file: " + sLog2FileSwitch
                    + LINE_SEP + "dir: " + (dir == null ? defaultDir : dir)
                    + LINE_SEP + "border: " + sLogBorderSwitch
                    + LINE_SEP + "consoleFilter: " + T[sConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[sFileFilter - V];
        }
    }

    public static void v(Object contents) {
        log(V, sGlobalTag, contents);
    }

    public static void v(String tag, Object... contents) {
        log(V, tag, contents);
    }

    public static void d(Object contents) {
        log(D, sGlobalTag, contents);
    }

    public static void d(String tag, Object... contents) {
        log(D, tag, contents);
    }

    public static void i(Object contents) {
        log(I, sGlobalTag, contents);
    }

    public static void i(String tag, Object... contents) {
        log(I, tag, contents);
    }

    public static void w(Object contents) {
        log(W, sGlobalTag, contents);
    }

    public static void w(String tag, Object... contents) {
        log(W, tag, contents);
    }

    public static void e(Object contents) {
        log(E, sGlobalTag, contents);
    }

    public static void e(String tag, Object... contents) {
        log(E, tag, contents);
    }

    public static void a(Object contents) {
        log(A, sGlobalTag, contents);
    }

    public static void a(String tag, Object... contents) {
        log(A, tag, contents);
    }

    public static void file(Object contents) {
        log(FILE | D, sGlobalTag, contents);
    }

    public static void file(@TYPE int type, Object contents) {
        log(FILE | type, sGlobalTag, contents);
    }

    public static void file(String tag, Object contents) {
        log(FILE | D, tag, contents);
    }

    public static void file(@TYPE int type, String tag, Object contents) {
        log(FILE | type, tag, contents);
    }

    public static void json(String contents) {
        log(JSON | D, sGlobalTag, contents);
    }

    public static void json(@TYPE int type, String contents) {
        log(JSON | type, sGlobalTag, contents);
    }

    public static void json(String tag, String contents) {
        log(JSON | D, tag, contents);
    }

    public static void json(@TYPE int type, String tag, String contents) {
        log(JSON | type, tag, contents);
    }

    public static void xml(String contents) {
        log(XML | D, sGlobalTag, contents);
    }

    public static void xml(@TYPE int type, String contents) {
        log(XML | type, sGlobalTag, contents);
    }

    public static void xml(String tag, String contents) {
        log(XML | D, tag, contents);
    }

    public static void xml(@TYPE int type, String tag, String contents) {
        log(XML | type, tag, contents);
    }

    private static void log(final int type, String tag, final Object... contents) {
        if (!sLogSwitch || (!sLog2ConsoleSwitch && !sLog2FileSwitch)) {
            return;
        }
        int type_low = type & 0x0f, type_high = type & 0xf0;
        if (type_low < sConsoleFilter && type_low < sFileFilter) {
            return;
        }
        final String[] tagAndHead = processTagAndHead(tag);
        String body = processBody(type_high, contents);
        if (sLog2ConsoleSwitch && type_low >= sConsoleFilter) {
            print2Console(type_low, tagAndHead[0], tagAndHead[1] + body);
        }
        if (sLog2FileSwitch || type_high == FILE) {
            if (type_low >= sFileFilter) {
                print2File(type_low, tagAndHead[0], tagAndHead[2] + body);
            }
        }
    }

    private static String[] processTagAndHead(String tag) {
        if (!sTagIsSpace && !sLogHeadSwitch) {
            tag = sGlobalTag;
        } else {
            StackTraceElement targetElement = Thread.currentThread().getStackTrace()[5];
            String className = targetElement.getClassName();
            String[] classNameInfo = className.split("\\.");
            if (classNameInfo.length > 0) {
                className = classNameInfo[classNameInfo.length - 1];
            }
            if (className.contains("$")) {
                className = className.split("\\$")[0];
            }
            if (sTagIsSpace) {
                tag = isSpace(tag) ? className : tag;
            }
            if (sLogHeadSwitch) {
                String head = new Formatter()
                        .format("%s, %s(%s.java:%d)",
                                Thread.currentThread().getName(),
                                targetElement.getMethodName(),
                                className,
                                targetElement.getLineNumber())
                        .toString();
                return new String[]{tag, head + LINE_SEP, " [" + head + "]: "};
            }
        }
        return new String[]{tag, "", ": "};
    }

    private static String processBody(int type, Object... contents) {
        String body = NULL_TIPS;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                body = object == null ? NULL : object.toString();
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ? NULL : content.toString())
                            .append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body;
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void print2Console(final int type, String tag, String msg) {
        if (sLogBorderSwitch) {
            print(type, tag, TOP_BORDER);
            msg = addLeftBorder(msg);
        }
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            print(type, tag, msg.substring(0, MAX_LEN));
            String sub;
            int index = MAX_LEN;
            for (int i = 1; i < countOfSub; i++) {
                sub = msg.substring(index, index + MAX_LEN);
                print(type, tag, sLogBorderSwitch ? LEFT_BORDER + sub : sub);
                index += MAX_LEN;
            }
            sub = msg.substring(index, len);
            print(type, tag, sLogBorderSwitch ? LEFT_BORDER + sub : sub);
        } else {
            print(type, tag, msg);
        }
        if (sLogBorderSwitch) {
            print(type, tag, BOTTOM_BORDER);
        }
    }

    private static void print(final int type, final String tag, String msg) {
        Log.println(type, tag, msg);
    }

    private static String addLeftBorder(String msg) {
        if (!sLogBorderSwitch) {
            return msg;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            sb.append(LEFT_BORDER).append(line).append(LINE_SEP);
        }
        return sb.toString();
    }

    private static void print2File(final int type, final String tag, final String msg) {
        Date now = new Date(System.currentTimeMillis());
        String format = FORMAT.format(now);
        String date = format.substring(0, 5);
        String time = format.substring(6);
        final String fullPath = (dir == null ? defaultDir : dir) + date + ".txt";
        if (!createOrExistsFile(fullPath)) {
            Log.e(tag, "log to " + fullPath + " failed!");
            return;
        }
        final String content = time + (T[type - V]) + ("/") + tag + msg + LINE_SEP;
        if (executor == null) {
           ThreadFactory threadFactory = new ThreadFactory() {
               @Override
               public Thread newThread(@NonNull Runnable r) {
                   return new Thread("logger_for_netwatch");
               }
           };
            executor = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),threadFactory);
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(fullPath, true));
                    bw.write(content);
                    Log.d(tag, "log to " + fullPath + " success!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(tag, "log to " + fullPath + " failed!");
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static boolean createOrExistsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] compress(byte input[]) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        try {
            compressor.setInput(input);
            compressor.finish();
            final byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

    public static byte[] uncompress(byte[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater deCompressor = new Inflater();
        try {
            deCompressor.setInput(input);
            final byte[] buf = new byte[2048];
            while (!deCompressor.finished()) {
                int count = 0;
                try {
                    count = deCompressor.inflate(buf);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
                bos.write(buf, 0, count);
            }
        } finally {
            deCompressor.end();
        }
        return bos.toByteArray();
    }

}
