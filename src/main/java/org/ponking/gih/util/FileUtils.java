package org.ponking.gih.util;

import org.ponking.gih.sign.Constant;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.*;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/7 13:13
 */
public class FileUtils {

    public static final String LOG_FILE_PATH;

    static {
        // 默认目录,因为云腾讯函数，只能在/tmp有读取日志权限，故手动设置腾讯云函数使用/tmp
        if (Constant.ENV_TENCENT_LOG_PATH.equals(System.getProperty(Constant.GENSHIN_ENV_LOG_PATH))) {
            LOG_FILE_PATH = Constant.ENV_TENCENT_LOG_PATH;
        } else {
            String baseDir = System.getProperty("user.dir");
            LOG_FILE_PATH = baseDir + File.separator + "logs";
        }
    }

    private FileUtils() {
    }

    public static String loadDaily(String path) {
        return loadDailyFile(path);
    }


    public static String loadDailyFile(String path) {
        FileInputStream fis = null;
        String log = "";
        try {
            fis = new FileInputStream(path);
            int size = fis.available();
            byte[] cache = new byte[size];
            fis.read(cache);
            log = new String(cache);
            return log;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return log;
    }


    public static void outPutSettingYaml(String fileName) {
        String baseDir = "";
        FileOutputStream fos = null;
        try {
            // 判读绝对路径
            if ("genshin-helper.yaml".equals(fileName)) {
                baseDir = System.getProperty("user.dir");
                fileName = baseDir + File.separator + fileName;
            }
            // 考虑到重复执行时候，第一次已经生成文件,无需再进行生成文件
            File outFile = new File(System.getProperty("user.dir") + File.separator + "genshin-helper-auto.yaml");

            if (outFile.exists()) {
                throw new RuntimeException("文件已存在:" + outFile.getPath());
            }

            GenshinHelperProperties pro = loadConfig(fileName);

            for (GenshinHelperProperties.Account account : pro.getAccount()) {
                String cookie = account.getCookie();
                Map<String, Object> user = GetstokenUtils.doGen(cookie);
                account.setStoken((String) user.get("stoken"));
                account.setStuid((String) user.get("stuid"));
            }

            Yaml outYaml = new Yaml();
            StringWriter writer = new StringWriter();
            String data = outYaml.dumpAs(pro, Tag.MAP, null);
            writer.write(data);
            fos = new FileOutputStream(outFile);
            fos.write(writer.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GenshinHelperProperties loadConfig(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不已存在：" + fileName);
        }
        InputStream is = new FileInputStream(file);
        Yaml yaml = new Yaml(new Constructor(GenshinHelperProperties.class));
        return yaml.load(is);
    }

    public static void clearDailyFile() {
        String path = System.getProperties().get("user.dir") + File.separator + "logs" + File.separator + "daily.log";
        File file = new File(path);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
